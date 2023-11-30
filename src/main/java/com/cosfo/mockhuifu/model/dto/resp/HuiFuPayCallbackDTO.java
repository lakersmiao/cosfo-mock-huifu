package com.cosfo.mockhuifu.model.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 汇付支付回调DTO
 * @author: George
 * @date: 2023-11-30
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuPayCallbackDTO {

    // 商户号
    private String acctId;

    // 分账信息
    private String acctSplitBunch;

    // 银行类型
    private String bankType;

    // 银行返回码
    private String bankCode;

    // 银行返回消息
    private String bankMessage;

    // 组合支付数据
    private String combinedPayData;

    // 组合支付手续费金额
    private String combinedPayFeeAmt;

    // 延迟记账标识
    private String delayAcctFlag;

    // 交易结束时间
    private String endTime;

    // 手续费金额
    private String feeAmount;

    // 手续费标识
    private String feeFlag;

    // 汇付交易流水号
    private String hfSeqId;

    // 汇付商户订单号
    private String huifuId;

    // 是否延迟记账
    private String isDelayAcct;

    // 是否分账
    private String isDiv;

    // 外部交易流水号
    private String outTransId;

    // 商户订单号
    private String partyOrderId;

    // 请求日期
    private String reqDate;

    // 请求流水号
    private String reqSeqId;

    // 响应码
    private String respCode;

    // 响应描述
    private String respDesc;

    // 结算金额
    private String settlementAmt;

    // 交易类型
    private String tradeType;

    // 交易金额
    private String transAmt;

    // 交易状态
    private String transStat;

    // 交易类型
    private String transType;

    // 微信返回信息
    private String wxResponse;

    // 交易手续费津贴信息
    private String transFeeAllowanceInfo;

    // 商户手续费账号
    private String feeAcctId;

    // 商户手续费汇付ID
    private String feeHuifuId;

}
