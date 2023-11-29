package com.cosfo.mockhuifu.service.impl;

import com.alibaba.fastjson.JSON;
import com.cosfo.mockhuifu.common.constant.HuiFuConstant;
import com.cosfo.mockhuifu.common.enums.DelayAcctFlagEnum;
import com.cosfo.mockhuifu.common.enums.ResponseCodeEnum;
import com.cosfo.mockhuifu.common.enums.TransStatEnum;
import com.cosfo.mockhuifu.common.utils.HuiFuGenerateSeqIdUtil;
import com.cosfo.mockhuifu.mapper.HuifuMockPaymentMapper;
import com.cosfo.mockhuifu.model.dto.request.HuiFuPayRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuPayResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;
import com.cosfo.mockhuifu.model.po.HuifuMockPayment;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.cosfo.mockhuifu.service.MockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
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
        insertMockPayment(payRequestDTO.getData(), huiFuResponseDTO.getData());

        // 4、返回结果
        String response = JSON.toJSONString(huiFuResponseDTO);
        log.info("处理模拟支付请求完毕，返回结果为：{}", response);

        // 5、定时任务扫描5s的支付单，模拟用户在这个时间段内完成了支付操作，进行下一步的金额增减且回调cosfo-mall
        return response;
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
            HuifuMockPayment huifuMockPayment = HuifuMockPayment.builder()
                    .reqDate(responseData.getReqDate())
                    .reqSeqId(requestData.getReqSeqId())
                    .transStat(responseData.getTransStat())
                    .huifuId(responseData.getHuifuId())
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
