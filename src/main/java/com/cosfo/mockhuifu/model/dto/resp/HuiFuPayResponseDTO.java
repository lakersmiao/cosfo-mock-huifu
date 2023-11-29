package com.cosfo.mockhuifu.model.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 汇付支付响应DTO
 * @author George
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuPayResponseDTO {

    /**
     * 业务响应码
     */
    private String respCode;

    /**
     * 业务响应信息
     */
    private String respDesc;

    /**
     * 请求时间
     */
    private String reqDate;

    /**
     * 请求流水号
     */
    private String reqSeqId;

    /**
     * 全局流水号
     */
    private String hfSeqId;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易类型，异步返回有区别
     */
    private String transType;

    /**
     * 交易金额
     */
    private BigDecimal transAmt;

    /**
     * 交易状态
     */
    private String transStat;

    /**
     * 商户号
     */
    private String huifuId;

    /**
     * 通道返回码
     */
    private String bankCode;

    /**
     * 通道返回描述
     */
    private String bankMessage;

    /**
     * 延时标记
     */
    private String delayAcctFlag;

    /**
     * js支付信息
     */
    private String payInfo;

    /**
     * 二维码链接
     */
    private String qrCode;

    /**
     * 支付宝返回的响应报文
     */
    private String alipayResponse;

    /**
     * 微信返回的响应报文
     */
    private String wxResponse;

    /**
     * 银联返回的响应报文
     */
    private String unionpayResponse;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账户号
     */
    private String acctId;

    /**
     * 结算金额
     */
    private String settlementAmt;

    /**
     * 手续费扣款标志
     */
    private Integer feeFlag;

    /**
     * 手续费金额
     */
    private String feeAmount;

    /**
     * 汇付侧交易完成时间
     */
    private String transFinshTime;

    /**
     * 支付完成时间
     */
    private String endTime;

    /**
     * 入账时间
     */
    private String acctDate;

    /**
     * 用户账单上的交易订单号
     */
    private String outTransId;

    /**
     * 用户账单上的商户订单号
     */
    private String partyOrderId;

    /**
     * 借贷记标识
     */
    private String debitFlag;

    /**
     * 是否分账交易
     */
    private String isDiv;

    /**
     * 分账对象
     */
    private String acctSplitBunch;

    /**
     * 是否延时交易
     */
    private String isDelayAcct;

    /**
     * 微信用户唯一标识码
     */
    private String wxUserId;

    /**
     * 商户终端定位
     */
    private String merDevLocation;

    /**
     * 手续费补贴信息
     */
    private String transFeeAllowanceInfo;

    /**
     * 营销补贴信息
     */
    private String combinedpayData;

    /**
     * 补贴部分的手续费
     */
    private String combinedpayFeeAmt;
}
