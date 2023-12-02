package com.cosfo.mockhuifu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.cosfo.mockhuifu.common.constant.HuiFuConstant;
import com.cosfo.mockhuifu.common.enums.DelayAcctFlagEnum;
import com.cosfo.mockhuifu.common.enums.ResponseCodeEnum;
import com.cosfo.mockhuifu.common.enums.TransStatEnum;
import com.cosfo.mockhuifu.common.utils.HuiFuGenerateSeqIdUtil;
import com.cosfo.mockhuifu.model.dto.request.HuiFuPayRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuBaseResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuPayCallbackResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuPayResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;
import com.cosfo.mockhuifu.model.po.HuiFuMockPayment;
import com.cosfo.mockhuifu.model.po.HuiFuMockTransactionSummary;
import com.cosfo.mockhuifu.repository.HuiFuMockTransactionSummaryRepository;
import com.cosfo.mockhuifu.repository.HuifuMockAccountRepository;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.cosfo.mockhuifu.service.HuiFuMockAccountService;
import com.cosfo.mockhuifu.service.MockService;
import com.github.rholder.retry.RetryerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @description: 模拟业务层
 * @author: George
 * @date: 2023-11-29
 **/
@Slf4j
@Service
public class MockServiceImpl implements MockService {

    @Resource
    private HuifuMockPaymentRepository huifuMockPaymentRepository;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private HuifuMockAccountRepository huifuMockAccountRepository;
    @Resource
    private HuiFuMockTransactionSummaryRepository huiFuMockTransactionSummaryRepository;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private HuiFuMockAccountService huiFuMockAccountService;

    @Value("${callback.maxRetryCnt}")
    private Integer maxRetryCnt;

    @Override
    public HuiFuResponseDTO mockJsApi(HuiFuRequestDTO<HuiFuPayRequestDTO> payRequestDTO) {
        log.info("接收到模拟支付请求，请求参数为：{}", payRequestDTO);
        // 1、参数校验
        //TODO：George 2023/11/29 前期先不做，仅是测试环境模拟

        // 2、组装模拟返回对象
        HuiFuResponseDTO<HuiFuPayResponseDTO> huiFuResponseDTO = assemblyMockResponse(payRequestDTO);

        // 3、插入模拟支付表
        Long paymentId = insertMockPayment(payRequestDTO.getData(), huiFuResponseDTO.getData());

        // 4、返回结果
        String response = JSON.toJSONString(huiFuResponseDTO);
        log.info("处理模拟支付请求完毕，返回结果为：{}", response);

        // 5、异步处理模拟用户支付成功后的通知
        ThreadUtil.execAsync(() -> {
            asyncMockUserPaySuccessNotify(paymentId);
        });
        return response;
    }


    public void asyncMockUserPaySuccessNotify(Long paymentId) {
        log.info("支付单：{}，等待用户支付中...", paymentId);
        // 这里简单睡3s，用于模拟用户支付的时间，和真实的支付间隔尽量保持一致
        ThreadUtil.sleep(3000);
        log.info("支付单：{}，用户支付完毕...", paymentId);

        HuiFuMockPayment huiFuMockPayment = huifuMockPaymentRepository.getById(paymentId);
        // 事务提交
        transactionTemplate.execute(status -> {
            Boolean result = true;
            try {
                // 1、账户逻辑的处理
                processAccountLogic(huiFuMockPayment);

                // 2、更改支付单的状态
                int updateStatResult = huifuMockPaymentRepository.updateStatusById(paymentId, TransStatEnum.PROCESSING.getStat(), TransStatEnum.SUCCESS.getStat());
                if (updateStatResult < 1) {
                    throw new RuntimeException("更新支付单状态失败");
                }
            } catch (Exception e) {
                log.error("模拟用户支付成功后的通知异常", e);
                status.setRollbackOnly();
                result = false;
            }
            return result;
        });

        // 3、异步给mall回调
        ThreadUtil.execAsync(() -> callbackMall(huiFuMockPayment));
    }

    private void callbackMall(HuiFuMockPayment huiFuMockPayment) {
        // 组装好回调的参数
        HuiFuBaseResponseDTO huiFuBaseResponseDTO = assemblyMockCallbackDTO(huiFuMockPayment);

        // 做个简易的回调重试 默认最多重试两次 间隔三秒
        RetryerBuilder<Boolean> retryerBuilder = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(BooleanUtils::isFalse)
                .withStopStrategy(attempt -> attempt.getAttemptNumber() > maxRetryCnt)
                .withWaitStrategy(attempt -> 3000L);
        try {
            retryerBuilder.build().call(() ->
                sendCallbackPostRequest(huiFuMockPayment, huiFuBaseResponseDTO)
            );
        } catch (Exception e) {
            log.error("模拟支付回调商城异常", e);
        }
    }

    /**
     * 发送回调请求
     *
     * @param huiFuMockPayment
     * @param huiFuBaseResponseDTO
     * @return
     */
    private Boolean sendCallbackPostRequest(HuiFuMockPayment huiFuMockPayment, HuiFuBaseResponseDTO huiFuBaseResponseDTO) {
        Map<String, Object> responseData = BeanUtil.beanToMap(huiFuBaseResponseDTO, true, true);
        HttpResponse response = HttpRequest.post(huiFuMockPayment.getNotifyUrl())
                .form(responseData)
                .execute();
        String callBackResponse = response.body();
        Boolean callBackSuccess = StringUtils.equals(callBackResponse, HuiFuConstant.JSAPI_CALLBACK_SUCCESS_PREFIX + huiFuMockPayment.getReqSeqId());
        log.info("模拟支付单：{}，回调商城：{}，返回参数：{}", huiFuMockPayment.getReqSeqId(), callBackSuccess ? "成功" : "失败", callBackResponse);
        return callBackSuccess;
    }

    private HuiFuBaseResponseDTO assemblyMockCallbackDTO(HuiFuMockPayment huiFuMockPayment) {
        // 手续费先硬编码成千23
        BigDecimal feeRate = BigDecimal.valueOf(0.023);
        BigDecimal feeAmt = huiFuMockPayment.getTransAmt().multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
        // 汇付返回的字段太多了，直接魔法值吧，改不动
        HuiFuPayCallbackResponseDTO huiFuPayCallbackResponseDTO = HuiFuPayCallbackResponseDTO.builder()
                .acctId("A18822546")
                .acctSplitBunch("{\"acct_infos\":[{\"acct_id\":\"A19126229\",\"div_amt\":\"0.02\",\"huifu_id\":\"6666000124879551\"}],\"fee_acct_id\":\"A19126229\",\"fee_amt\":\"0.00\",\"fee_huifu_id\":\"6666000124879551\"}")
                .acctStat("I")
                .atuSubMerId("553194110")
                .avoidSmsFlag("")
                .bagentId("6666000124683186")
                .bankCode("SUCCESS")
                .bankDesc("交易成功")
                .bankMessage("交易成功")
                .bankOrderNo("4200001974202311305397932158")
                .bankSeqId("442560")
                .bankType("OTHERS")
                .baseAcctId("A18822486")
                .batchId("231130")
                .channelType("U")
                .chargeFlags("758_0")
                .combinedpayData("[]")
                .combinedpayFeeAmt("0.00")
                .debitType("0")
                .delayAcctFlag("Y")
                .divFlag("0")
                .endTime("20231130092615")
                .feeAmount(feeAmt.toPlainString())
                .feeAmt(feeAmt.toPlainString())
                .feeFlag("2")
                .feeFormulaInfos("[{\"fee_formula\":\"AMT*0.0023\",\"fee_type\":\"TRANS_FEE\"}]")
                .feeRecType("1")
                .feeType("INNER")
                .gateId("VT")
                .hfSeqId(huiFuMockPayment.getHfSeqId())
                .huifuId(huiFuMockPayment.getHuiFuId())
                .isDelayAcct("1")
                .isDiv("0")
                .merName("客思服(杭州)科技有限公司")
                .merOrdId("P1730035457365442560")
                .mypaytsfDiscount("0.00")
                .needBigObject("false")
                .notifyType("1")
                .orgAuthNo("")
                .orgHuifuSeqId("")
                .orgTransDate("")
                .outOrdId("4200001974202311305397932158")
                .outTransId("4200001974202311305397932158")
                .partyOrderId("03232311303397044709485")
                .payAmt(huiFuMockPayment.getTransAmt().toPlainString())
                .payScene("02")
                .pospSeqId("03232311303397044709485")
                .productId("PAYUN")
                .refNo("092610442560")
                .reqDate(huiFuMockPayment.getReqDate())
                .reqSeqId(huiFuMockPayment.getReqSeqId())
                .respCode("00000000")
                .respDesc("交易成功")
                .riskCheckData("{}")
                .riskCheckInfo("{}")
                .settlementAmt(huiFuMockPayment.getTransAmt().toPlainString())
                .subRespCode("00000000")
                .subRespDesc("交易成功")
                .subsidyStat("I")
                .sysId(huiFuMockPayment.getHuiFuId())
                .tradeType(huiFuMockPayment.getTradeType())
                .transAmt(huiFuMockPayment.getTransAmt().toPlainString())
                .transDate(huiFuMockPayment.getReqDate())
                .transFeeAllowanceInfo("{\"actual_fee_amt\":\"0.00\",\"allowance_fee_amt\":\"0.00\",\"allowance_type\":\"0\",\"receivable_fee_amt\":\"0.00\"}")
                .transStat("S")
                .transTime("092610")
                .transType(huiFuMockPayment.getTradeType())
                .wxResponse("{\"bank_type\":\"OTHERS\",\"coupon_fee\":\"0.00\",\"openid\":\"o8jhot7NjCGR_eRsU1JuF7jWzdu8\",\"sub_appid\":\"wxda8819c87e1e69c8\",\"sub_openid\":\"o3geR6grjvmN33eTPFgs9Bhu9MCI\"}")
                .build();
        return HuiFuBaseResponseDTO.builder()
                .respCode(ResponseCodeEnum.MOCK_JSAPI_CALLBACK_SUCCESS.getCode())
                .respData(JSON.toJSONString(huiFuPayCallbackResponseDTO))
                .respDesc("交易成功[000]")
                .sign("GDlHn4FnqlycoPxL4WyVIv2UnU2RvyUfXkoO30FBoxPAWaDDIZpKRn+s6IAk4INTyVYpxYjxHkarSPZ/7JcGD5BmHBPK5zx+vIDnAPtsqTXH18Rtb5wTmva1s+RnGd5bIxD4h9xxlu6xj1iINasoc/bGR4OiHMdVUlaOZIchzRjtmgDJdkS6/StqbyKatYqckmaLlAbkKPTNfV9AkP8dJAHYEwgW4h3+ZufqG7Toxy7GCP0IBBmqoSClSNadsIHKTAOUAsyX920EPPtWn84eqrsJzEXKjcn1DhjFSmvrq14qk4FdcqzcFr8Jr9vqiviWjYTz/YUpWg6lXhjyzNDiTA==")
                .build();
    }

    private void processAccountLogic(HuiFuMockPayment huiFuMockPayment) {
        String huiFuId = huiFuMockPayment.getHuiFuId();
        // 1、初始化汇付账户
        huiFuMockAccountService.initAccount(huiFuId);
        // 2、汇付账户金额增加
        int increaseResult = huifuMockAccountRepository.increaseDelayedAmt(huiFuId, huiFuMockPayment.getTransAmt());
        if (increaseResult < 1) {
            throw new RuntimeException("更新汇付延迟账户金额失败");
        }
        log.info("汇付延迟账户：{}，增加金额：{}元", huiFuId, huiFuMockPayment.getTransAmt());

        // 2、汇付模拟的交易汇总金额增加
        HuiFuMockTransactionSummary summary = new HuiFuMockTransactionSummary();
        summary.setOrgReqSeqId(huiFuMockPayment.getReqSeqId());
        summary.setOrgAmt(huiFuMockPayment.getTransAmt());
        summary.setDelayedAmt(huiFuMockPayment.getTransAmt());
        summary.setConfirmedAmt(BigDecimal.ZERO);
        summary.setRefundAmt(BigDecimal.ZERO);
        huiFuMockTransactionSummaryRepository.save(summary);
    }

    /**
     * 保存模拟支付单
     *
     * @param requestData
     * @param responseData
     */
    private Long insertMockPayment(HuiFuPayRequestDTO requestData, HuiFuPayResponseDTO responseData) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            HuiFuMockPayment huifuMockPayment = HuiFuMockPayment.builder()
                    .reqDate(responseData.getReqDate())
                    .reqSeqId(requestData.getReqSeqId())
                    .hfSeqId(responseData.getHfSeqId())
                    .transStat(responseData.getTransStat())
                    .huiFuId(responseData.getHuifuId())
                    .tradeType(responseData.getTradeType())
                    .transAmt(responseData.getTransAmt())
                    .timeExpire(requestData.getTimeExpire())
                    .delayAcctFlag(DelayAcctFlagEnum.DELAY.getFlag())
                    //TODO：George 2023/11/29 回调地址先硬编码后续优化
                    .notifyUrl("http://localhost:8081/pay-notify/huifu-pay")
                    .build();
            huifuMockPaymentRepository.save(huifuMockPayment);
            transactionManager.commit(status);
            return huifuMockPayment.getId();
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("保存模拟支付记录失败", e);
            throw new RuntimeException("保存模拟支付记录失败");
        }
    }

    private HuiFuResponseDTO<HuiFuPayResponseDTO> assemblyMockResponse(HuiFuRequestDTO<HuiFuPayRequestDTO> request) {
        HuiFuPayRequestDTO requestData = request.getData();

        HuiFuPayResponseDTO responseData = HuiFuPayResponseDTO.builder()
                .bankCode("SUCCESS")
                .bankMessage("成功")
                .delayAcctFlag("Y")
                .hfSeqId(HuiFuGenerateSeqIdUtil.generateHfSeqId())
                .huifuId(requestData.getHuiFuId())
                .partyOrderId(null)
                .payInfo("{\"appId\":\"wxda8819c87e1e69c8\",\"timeStamp\":\"1701493917\",\"nonceStr\":\"cfcbab82534343a3906f0de97c6107c6\",\"package\":\"prepay_id=wx021311571756632c21ee2d94c8e4410000\",\"signType\":\"RSA\",\"paySign\":\"IWa/fCrWQj2DvzHJRutYekIKxJfxZ/EU3/KFepIkWEjUg/sZ1VarUuQ25jqaiY7Iv6wrjv6uulQIdk+QQr2xarlJowBJ1e1VsHUCm5g2lXBsEzHV4v82Xogb1XYgZ2M96TbNdqNNqMcdhgNS4WNgsGeDEAsGx9pHbOLgjNmxpALgzQ6cBXSkaKp/UJPpsqRrbor7JCsDu9KR3v63MZCG571ulIpUH6W7ol8cBccx0kVlhbquMkjMkH83hi8inlrSJXi6+Q/zvimxDehPHm+dfoxUJGqLvygSkOaSzWLuqCoPATwfrhzXSTjnWJRUmctaDb00VGL99EPwufN8TVAOBw==\"}")
                .reqDate(requestData.getReqDate())
                .reqSeqId(requestData.getReqSeqId())
                .respCode(ResponseCodeEnum.MOCK_JSAPI_SUCCESS.getCode())
                .respDesc(ResponseCodeEnum.MOCK_JSAPI_SUCCESS.getDesc())
                .tradeType(requestData.getTradeType())
                .transAmt(requestData.getTransAmt())
                .transStat(TransStatEnum.PROCESSING.getStat()).build();

        HuiFuResponseDTO<HuiFuPayResponseDTO> response = new HuiFuResponseDTO<>();
        response.setSysId(requestData.getHuiFuId());
        response.setProductId(HuiFuConstant.PRODUCT_ID);
        response.setSign("");
        response.setData(responseData);
        return response;
    }
}
