package com.cosfo.mockhuifu.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.cosfo.mockhuifu.common.constant.HuiFuConstant;
import com.cosfo.mockhuifu.common.enums.DelayAcctFlagEnum;
import com.cosfo.mockhuifu.common.enums.ResponseCodeEnum;
import com.cosfo.mockhuifu.common.enums.TransStatEnum;
import com.cosfo.mockhuifu.common.utils.HuiFuGenerateSeqIdUtil;
import com.cosfo.mockhuifu.model.dto.request.HuiFuPayRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuPayResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;
import com.cosfo.mockhuifu.model.po.HuiFuMockPayment;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.cosfo.mockhuifu.service.MockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;

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

    @Override
    public String mockJsApi(HuiFuRequestDTO<HuiFuPayRequestDTO> payRequestDTO) {
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


    private void asyncMockUserPaySuccessNotify(Long paymentId) {
        log.info("支付单：{}，等待用户支付中...", paymentId);
        // 这里简单睡3s，用于模拟用户支付的时间，和真实的支付间隔尽量保持一致
        ThreadUtil.sleep(3000);
        log.info("支付单：{}，用户支付完毕...", paymentId);

        // 1、账户逻辑的处理
        processAccountLogic(paymentId);

        // 2、更改支付单的状态
        int updateStatResult = huifuMockPaymentRepository.updateStatusById(paymentId, TransStatEnum.PROCESSING.getStat(), TransStatEnum.SUCCESS.getStat());
        if (updateStatResult < 1) {
            throw new RuntimeException("更新支付单状态失败");
        }

        // 3、给cosfo-mall回调
        //TODO：George 2023/11/29 回调逻辑待补充
    }

    private void processAccountLogic(Long paymentId) {
        //TODO：George 2023/11/29 总账户金额更新、支付单维度的金额更新
    }

    private void sendMQForMockUserPaySuccessNotify(Long paymentId) {
        // 因为测试环境的延迟消息等级是30s、1min起步，真实用户也就三五秒支付完毕有回调通知
        // 这里简单处理睡3s，然后发送MQ消息
        log.info("支付单：{}，等待用户支付中...", paymentId);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("睡眠异常", e);
        }
        log.info("支付单：{}，用户支付完毕，", paymentId);



    }

    /**
     * 保存模拟支付单
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
                .hfSeqId(HuiFuGenerateSeqIdUtil.generateJsapiSeqId())
                .huifuId(requestData.getHuiFuId())
                .partyOrderId(null)
                .payInfo(null)
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
