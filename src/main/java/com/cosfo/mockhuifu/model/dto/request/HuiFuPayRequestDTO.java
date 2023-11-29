package com.cosfo.mockhuifu.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 汇付支付请求DTO
 * @author: George
 * @date: 2023-11-29
 **/
@Data
public class HuiFuPayRequestDTO {

    /**
     * 请求日期
     */
    private String reqDate;
    /**
     * 请求流水号
     */
    private String reqSeqId;
    /**
     * 商户号
     */
    private String huiFuId;
    /**
     * 交易类型
     */
    private String tradeType;
    /**
     * 交易金额
     */
    private BigDecimal transAmt;
    /**
     * 商品描述
     */
    private String goodsDesc;
    /**
     * 交易有效期
     */
    private String timeExpire;
    /**
     * 禁用信用卡标记
     */
    private String limitPayType;
    /**
     * 是否延迟交易
     */
    private String delayAcctFlag;
    /**
     * 渠道号
     */
    private String channelNo;
    /**
     * 手续费扣款标志
     */
    private String feeFlag;
    /**
     * 场景类型
     */
    private String payScene;
    /**
     * 安全信息
     */
    private String riskCheckData;
    /**
     * 设备信息
     */
    private String terminalDeviceData;
    /**
     * 分账对象
     */
    private String acctSplitBunch;
    /**
     * 传入分帐遇到优惠的处理规则
     */
    private String termDivCouponType;
    /**
     * 聚合正扫微信拓展参数集合
     */
    private String wxData;
    /**
     * 支付宝扩展参数集合
     */
    private String alipayData;
    /**
     * 银联参数集合
     */
    private String unionpayData;
    /**
     * 数字人民币参数集合
     */
    private String dcData;
    /**
     * 商户贴息标记
     */
    private String fqMerDiscountFlag;
    /**
     * 异步通知地址
     */
    private String notifyUrl;
    /**
     * 备注
     */
    private String remark;
    /**
     * 账户号
     */
    private String acctId;
}
