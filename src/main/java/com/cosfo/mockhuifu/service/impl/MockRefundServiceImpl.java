package com.cosfo.mockhuifu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.cosfo.mockhuifu.common.constant.HuiFuConstant;
import com.cosfo.mockhuifu.common.enums.ResponseCodeEnum;
import com.cosfo.mockhuifu.common.enums.TransStatEnum;
import com.cosfo.mockhuifu.common.exception.BizException;
import com.cosfo.mockhuifu.common.exception.ParamsException;
import com.cosfo.mockhuifu.common.utils.HuiFuGenerateSeqIdUtil;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRefundRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.*;
import com.cosfo.mockhuifu.model.po.HuiFuMockPayment;
import com.cosfo.mockhuifu.model.po.HuiFuMockRefund;
import com.cosfo.mockhuifu.repository.HuiFuMockTransactionSummaryRepository;
import com.cosfo.mockhuifu.repository.HuifuMockAccountRepository;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.cosfo.mockhuifu.repository.HuiFuMockRefundRepository;
import com.cosfo.mockhuifu.service.MockRefundService;
import com.github.rholder.retry.RetryerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @description: 模拟退款业务层实现类
 * @author: George
 * @date: 2023-12-03
 **/
@Slf4j
@Service
public class MockRefundServiceImpl implements MockRefundService {

    @Resource
    private HuifuMockPaymentRepository huifuMockPaymentRepository;
    @Resource
    private HuiFuMockRefundRepository huifuMockRefundRepository;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private HuifuMockAccountRepository huifuMockAccountRepository;
    @Resource
    private HuiFuMockTransactionSummaryRepository huiFuMockTransactionSummaryRepository;

    @Value("${callback.maxRetryCnt}")
    private Integer maxRetryCnt;

    @Override
    public HuiFuResponseDTO mockRefund(HuiFuRequestDTO<HuiFuRefundRequestDTO> refundRequestDTO) {
        log.info("模拟退款请求参数：{}", refundRequestDTO);
        // 预检验参数
        preProcess(refundRequestDTO);

        // 分布式锁

        // 生成全局序列号
        String hfSeqId = HuiFuGenerateSeqIdUtil.generateRefundHfSeqId();
        refundRequestDTO.getData().setHfSeqId(hfSeqId);

        // 金额校验
        verifyAmtIsSufficient(refundRequestDTO);

        // 记录退款
        Long refundId = insertRefund(refundRequestDTO.getData());

        // 组装返回结果
        HuiFuResponseDTO<HuiFuRefundResponseDTO> response = assemblyRefundResponseDTO(refundRequestDTO.getData());

        // 异步模拟退款然后回调mall
        ThreadUtil.execAsync(() -> {
            asyncMockRefundSuccessNotify(refundId);
        });

        return response;
    }

    private void asyncMockRefundSuccessNotify(Long refundId) {
        HuiFuMockRefund huiFuMockRefund = huifuMockRefundRepository.getById(refundId);
        transactionTemplate.execute(status -> {
            Boolean result = true;
            try {
                // 1、账户逻辑的处理
                processAccountLogic(huiFuMockRefund);

                // 2、更改退款单的状态
                int updateStatResult = huifuMockRefundRepository.updateStatusById(refundId, TransStatEnum.PROCESSING.getStat(), TransStatEnum.SUCCESS.getStat());
                if (updateStatResult < 1) {
                    throw new RuntimeException("更新退款单状态失败");
                }
            } catch (Exception e) {
                log.error("模拟用户退款成功后的通知异常", e);
                status.setRollbackOnly();
                result = false;
            }
            return result;
        });

        // 3、异步给mall回调
        ThreadUtil.execAsync(() -> callbackMall(huiFuMockRefund));
    }

    private void callbackMall(HuiFuMockRefund huiFuMockRefund) {
        // 组装好回调的参数
        HuiFuBaseResponseDTO huiFuBaseResponseDTO = assemblyMockCallbackDTO(huiFuMockRefund);

        // 做个简易的回调重试 默认最多重试两次 间隔三秒
        RetryerBuilder<Boolean> retryerBuilder = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(BooleanUtils::isFalse)
                .withStopStrategy(attempt -> attempt.getAttemptNumber() > maxRetryCnt)
                .withWaitStrategy(attempt -> 3000L);
        try {
            retryerBuilder.build().call(() ->
                    sendCallbackPostRequest(huiFuMockRefund, huiFuBaseResponseDTO)
            );
        } catch (Exception e) {
            log.error("模拟退款回调商城异常", e);
        }
    }

    private Boolean sendCallbackPostRequest(HuiFuMockRefund huiFuMockRefund, HuiFuBaseResponseDTO huiFuBaseResponseDTO) {
        Map<String, Object> responseData = BeanUtil.beanToMap(huiFuBaseResponseDTO, true, true);
        log.info("模拟退款单：{}，回调商城：{}，请求参数：{}", huiFuMockRefund.getReqSeqId(), huiFuMockRefund.getNotifyUrl(), responseData);
        HttpResponse response = HttpRequest.post(huiFuMockRefund.getNotifyUrl())
                .form(responseData)
                .execute();
        String callBackResponse = response.body();
        Boolean callBackSuccess = StringUtils.equals(callBackResponse, HuiFuConstant.CALLBACK_SUCCESS_PREFIX + huiFuMockRefund.getReqSeqId());
        log.info("模拟退款单：{}，回调商城：{}，返回参数：{}", huiFuMockRefund.getReqSeqId(), callBackSuccess ? "成功" : "失败", callBackResponse);
        return callBackSuccess;
    }

    private HuiFuBaseResponseDTO assemblyMockCallbackDTO(HuiFuMockRefund huiFuMockRefund) {
        HuiFuRefundCallBackResponseDTO huiFuRefundCallBackResponseDTO = HuiFuRefundCallBackResponseDTO.builder()
                .acctId("A18822546")
                .acctSplitBunch("{\"acct_infos\":[{\"acct_date\":\"20231203\",\"acct_id\":\"A25055786\",\"div_amt\":\"0.03\",\"huifu_id\":\"6666000124877554\"}],\"fee_acct_date\":\"\",\"fee_acct_id\":\"A25055786\",\"fee_amt\":\"0.00\",\"fee_huifu_id\":\"6666000124877554\"}")
                .actualRefAmt("0.03")
                .atuSubMerId("582174084")
                .authNo("")
                .bagentId("6666000124683186")
                .bankCode("SUCCESS")
                .bankDesc("退款成功")
                .bankMessage("退款成功")
                .bankSeqId("053118")
                .bankType("OTHERS")
                .baseAcctId("A18822546")
                .batchId("231203")
                .combinedpayData("")
                .combinedpayFeeAmt("0.00")
                .devsId("")
                .endTime("20231203140709")
                .hfSeqId(huiFuMockRefund.getHfSeqId())
                .huifuId(huiFuMockRefund.getHuiFuId())
                .merName("客思服(杭州)科技有限公司")
                .merOperId("")
                .merOrdId(huiFuMockRefund.getReqSeqId())
                .merPriv("")
                .mypaytsfDiscount("0.00")
                .needBigObject("false")
                .ordAmt(huiFuMockRefund.getRefundAmt().toPlainString())
                .orderId("202312031407000TOP1_Bit507833823")
                .orgAllowanceType("0")
                .orgFeeAmt("0.00")
                .orgFeeFlag("2")
                .orgFeeRecType("1")
                .orgOrdAmt("0.03")
                .orgReqDate(huiFuMockRefund.getOrgReqDate())
                .orgReqSeqId(huiFuMockRefund.getOrgReqSeqId())
                .orgTermOrdId(huiFuMockRefund.getOrgReqSeqId())
                .outOrdId("")
                .partyOrderId("03222312035074799912213")
                .productId("PAYUN")
                .refCut("1")
                .refNo("140700053118")
                .remark("")
                .reqDate(huiFuMockRefund.getReqDate())
                .reqSeqId(huiFuMockRefund.getReqSeqId())
                .subRespCode("00000000")
                .subRespDesc("交易成功")
                .sysId("6666000124877554")
                .totalRefAmt("0.03")
                .totalRefFeeAmt("0.00")
                .transDate("20231203")
                .transFinishTime("20231203140709")
                .transStat("S")
                .transTime("140700")
                .transType("TRANS_REFUND")
                .build();

        return HuiFuBaseResponseDTO.builder()
                .respCode(ResponseCodeEnum.MOCK_REFUND_CALLBACK_SUCCESS.getCode())
                .respData(JSON.toJSONString(huiFuRefundCallBackResponseDTO))
                .respDesc("交易成功[000]")
                .sign("VOieRNc9R5F+vmwDnZceRohA1wk/Tve+SvpKGFuZg2PrA4qmSAXXZU8+mXJjVOyTfSFevf4fzjs9KCqC3EJIHY3DfUomluBkMN2IPo2gO8+1dohWjnOZgmTtuEo0tA2vMznejRwH7WFPj1+Wx+YQI+pgslD0x1+P+ItNNVKpjc5Vl0edcT86A8+aYeGuG3hzCWHfHPWUt0k+pLPcWC3JgEcwuTgMc7ROHdUGrTuut7iwPoQtU1JfrSs17yQ9OOr7wODl076t4BePjNqaLQX0qMNLQ/Bqq9G5AowPkxh4CAT6A1OXfqCsVCpiQiaSJXyBhSZQ30n8UMv45veGDWRvww==")
                .build();
    }

    private void processAccountLogic(HuiFuMockRefund huiFuMockRefund) {
        // 从延迟户里退款
        int increaseAccountResult = huifuMockAccountRepository.increaseDelayedAmt(huiFuMockRefund.getHuiFuId(), huiFuMockRefund.getRefundAmt().negate());
        if (increaseAccountResult < 1) {
            throw new RuntimeException("扣减延迟户金额失败");
        }
        // 记录交易汇总
        int increaseSummaryResult = huiFuMockTransactionSummaryRepository.increaseDelayedAmt(huiFuMockRefund.getOrgReqDate(), huiFuMockRefund.getOrgReqSeqId(), huiFuMockRefund.getRefundAmt().negate());
        if (increaseSummaryResult < 1) {
            throw new RuntimeException("扣减交易汇总失败");
        }
    }

    private HuiFuResponseDTO<HuiFuRefundResponseDTO> assemblyRefundResponseDTO(HuiFuRefundRequestDTO requestDTO) {
        HuiFuRefundResponseDTO huiFuRefundResponseDTO = HuiFuRefundResponseDTO.builder()
                .acctSplitBunch(null)
                .bankAcceptedFlag("Y")
                .bankCode("")
                .bankMessage("")
                .hfSeqId(requestDTO.getHfSeqId())
                .huiFuId(requestDTO.getHuiFuId())
                .ordAmt(requestDTO.getOrderAmt())
                .orgHfSeqId(requestDTO.getOrgHfSeqId())
                .orgReqDate(requestDTO.getOrgReqDate())
                .orgReqSeqId(requestDTO.getOrgReqSeqId())
                .respCode(ResponseCodeEnum.MOCK_REFUND_PROCESSING.getCode())
                .respDesc(ResponseCodeEnum.MOCK_REFUND_PROCESSING.getDesc())
                .transDate(requestDTO.getReqDate())
                .transStat(TransStatEnum.PROCESSING.getStat())
                .transTime(null).build();

        HuiFuResponseDTO<HuiFuRefundResponseDTO> responseDTO = new HuiFuResponseDTO<>();
        responseDTO.setData(huiFuRefundResponseDTO);
        responseDTO.setProductId(HuiFuConstant.PRODUCT_ID);
        responseDTO.setSign("kGFGDY5J5EVZEWAyg7fY7H6o71vvoQGW3IOa5clEX2PJlvAn49STfblYp2tDR5xkOW85O++AKZfR1GUI77+FQYbOKz0LIm2NbHA6o06uBBbsVpHHnqN8QTqe5nlcDGmpU757FEBSYhlViHkLmWMJsQAGA3lgpXByy+SqI+7AKpM5rdo5dmGzWvgc4/QUS6WB2aWWv6GkXb7yql4Y7SWCWLsPE1b7HI/pGvg9CrshH582MpQ3s8/+0AXtUSieZwZVI2EXAeYi0aajfPq6AGjU/wvZjvFmzQrN55X/O3jhkY86GGqdlLQNhCkKu3cU07cdzvkFgMJNSWjw3Ewvt2AZAA==");
        responseDTO.setSysId(requestDTO.getHuiFuId());
        return responseDTO;
    }

    private Long insertRefund(HuiFuRefundRequestDTO requestData) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            HuiFuMockRefund huiFuMockRefund = HuiFuMockRefund.builder()
                    .huiFuId(requestData.getHuiFuId())
                    .notifyUrl("http://localhost:8081/pay-notify/huifu-refund")
                    .refundAmt(new BigDecimal(requestData.getOrderAmt()))
                    .orgReqDate(requestData.getOrgReqDate())
                    .orgReqSeqId(requestData.getOrgReqSeqId())
                    .reqDate(requestData.getReqDate())
                    .reqSeqId(requestData.getReqSeqId())
                    .transStat(TransStatEnum.PROCESSING.getStat())
                    .hfSeqId(requestData.getHfSeqId())
                    .build();
            huifuMockRefundRepository.save(huiFuMockRefund);
            transactionManager.commit(status);
            return huiFuMockRefund.getId();
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("保存退款记录失败", e);
            throw e;
        }
    }

    private void verifyAmtIsSufficient(HuiFuRequestDTO<HuiFuRefundRequestDTO> refundRequestDTO) {
        HuiFuRefundRequestDTO requestData = refundRequestDTO.getData();
        String orgReqDate = requestData.getOrgReqDate();
        String orgReqSeqId = requestData.getOrgReqSeqId();
        HuiFuMockPayment huiFuMockPayment = huifuMockPaymentRepository.queryByReqDateAndReqSeqId(orgReqDate, orgReqSeqId, TransStatEnum.SUCCESS.getStat());
        if (huiFuMockPayment == null) {
            throw new BizException(String.format("根据请求日期[%s]和请求流水号[%s]未查询到支付单", orgReqDate, orgReqSeqId));
        }
        BigDecimal totalRefundAmt = huifuMockRefundRepository.queryTotalRefundAmtByReqDateAndReqSeqId(orgReqDate, orgReqSeqId, TransStatEnum.SUCCESS.getStat());
        BigDecimal refundAmt = new BigDecimal(requestData.getOrderAmt());
        if (totalRefundAmt.add(refundAmt).compareTo(huiFuMockPayment.getTransAmt()) > 0) {
            throw new BizException(String.format("退款金额[%s]大于支付单[%s]的支付金额[%s]", refundAmt, orgReqSeqId, huiFuMockPayment.getTransAmt()));
        }
    }

    private void preProcess(HuiFuRequestDTO<HuiFuRefundRequestDTO> refundRequestDTO) {
        if (refundRequestDTO == null) {
            throw new ParamsException("交易退款请求参数不能为空");
        }
        HuiFuRefundRequestDTO requestData = refundRequestDTO.getData();
        if (requestData == null) {
            throw new ParamsException("交易退款请求参数不能为空");
        }
        String huiFuId = requestData.getHuiFuId();
        if (StringUtils.isEmpty(huiFuId)) {
            throw new ParamsException("汇付id不能为空");
        }
        if (StringUtils.isEmpty(requestData.getNotifyUrl())) {
            throw new ParamsException("回调地址不能为空");
        }
        if (StringUtils.isEmpty(requestData.getOrderAmt())) {
            throw new ParamsException("退款金额不能为空");
        }
        try {
            BigDecimal orderAmt = new BigDecimal(requestData.getOrderAmt());
            if (orderAmt.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ParamsException("退款金额必须大于0");
            }
            if (orderAmt.scale() > 2) {
                throw new ParamsException("退款金额格式不正确");
            }
        } catch (NumberFormatException e) {
            throw new ParamsException("退款金额格式不正确");
        }
        if (StringUtils.isEmpty(requestData.getOrgReqDate())) {
            throw new ParamsException("原交易请求日期不能为空");
        }
        if (StringUtils.isEmpty(requestData.getOrgReqSeqId())) {
            throw new ParamsException("原交易请求流水号不能为空");
        }
        if (StringUtils.isEmpty(requestData.getReqDate())) {
            throw new ParamsException("退款请求日期不能为空");
        }
        if (StringUtils.isEmpty(requestData.getReqSeqId())) {
            throw new ParamsException("退款请求流水号不能为空");
        }
    }
}
