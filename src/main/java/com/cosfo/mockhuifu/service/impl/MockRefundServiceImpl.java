package com.cosfo.mockhuifu.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.cosfo.mockhuifu.common.constant.HuiFuConstant;
import com.cosfo.mockhuifu.common.enums.ResponseCodeEnum;
import com.cosfo.mockhuifu.common.enums.TransStatEnum;
import com.cosfo.mockhuifu.common.exception.BizException;
import com.cosfo.mockhuifu.common.exception.ParamsException;
import com.cosfo.mockhuifu.common.utils.HuiFuGenerateSeqIdUtil;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRefundRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuRefundResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;
import com.cosfo.mockhuifu.model.po.HuiFuMockPayment;
import com.cosfo.mockhuifu.model.po.HuiFuMockRefund;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.cosfo.mockhuifu.repository.HuiFuMockRefundRepository;
import com.cosfo.mockhuifu.service.MockRefundService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.math.BigDecimal;

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

    @Override
    public HuiFuResponseDTO mockRefund(HuiFuRequestDTO<HuiFuRefundRequestDTO> refundRequestDTO) {
        // 预检验参数
        preProcess(refundRequestDTO);

        // 分布式锁

        // 生成全局序列号
        String hfSeqId = HuiFuGenerateSeqIdUtil.generateRefundHfSeqId();
        refundRequestDTO.getData().setHuiFuId(hfSeqId);

        // 金额校验
        verifyAmtIsSufficient(refundRequestDTO);

        // 记录退款
        insertRefund(refundRequestDTO.getData());

        // 组装返回结果
        HuiFuResponseDTO<HuiFuRefundResponseDTO> response = assemblyRefundResponseDTO(refundRequestDTO.getData());

        // 异步模拟退款然后回调mall
//        ThreadUtil.execAsync(() -> {
//            asyncMockRefundSuccessNotify();
//        });

        return response;
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

    private void insertRefund(HuiFuRefundRequestDTO requestData) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            HuiFuMockRefund huiFuMockRefund = HuiFuMockRefund.builder()
                    .huiFuId(requestData.getHuiFuId())
                    .notifyUrl(requestData.getNotifyUrl())
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
