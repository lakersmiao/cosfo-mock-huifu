package com.cosfo.mockhuifu.model.dto.resp;

import com.alibaba.fastjson.annotation.JSONField;
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
public class HuiFuPayCallbackResponseDTO {

    // 账户ID
    private String acctId;

    // 账户分组信息
    private String acctSplitBunch;

    // 账户状态
    private String acctStat;

    // 授权子商户ID
    private String atuSubMerId;

    // 避免短信标志
    private String avoidSmsFlag;

    // 代理商ID
    private String bagentId;

    // 银行代码
    private String bankCode;

    // 银行描述
    private String bankDesc;

    // 银行消息
    private String bankMessage;

    // 银行订单号
    private String bankOrderNo;

    // 银行流水号
    private String bankSeqId;

    // 银行类型
    private String bankType;

    // 基础账户ID
    private String baseAcctId;

    // 批次ID
    private String batchId;

    // 渠道类型
    private String channelType;

    // 收费标志
    private String chargeFlags;

    // 组合支付数据
    private String combinedpayData;

    // 组合支付费用金额
    private String combinedpayFeeAmt;

    // 借记类型
    private String debitType;

    // 延迟账户标志
    private String delayAcctFlag;

    // 分账标志
    private String divFlag;

    // 结束时间
    private String endTime;

    // 手续费金额
    private String feeAmount;

    // 费用金额
    private String feeAmt;

    // 费用标志
    private String feeFlag;

    // 费用公式信息
    private String feeFormulaInfos;

    // 费用记录类型
    private String feeRecType;

    // 费用类型
    private String feeType;

    // 网关ID
    private String gateId;

    // 汇付流水号
    private String hfSeqId;

    // 汇付ID
    private String huifuId;

    // 是否延迟账户
    private String isDelayAcct;

    // 是否分账
    private String isDiv;

    // 商户名称
    private String merName;

    // 商户订单ID
    private String merOrdId;

    // 我的支付折扣
    private String mypaytsfDiscount;

    // 是否需要大对象
    private String needBigObject;

    // 通知类型
    private String notifyType;

    // 原授权号
    private String orgAuthNo;

    // 原汇付流水号
    private String orgHuifuSeqId;

    // 原交易日期
    private String orgTransDate;

    // 外部订单ID
    private String outOrdId;

    // 外部交易ID
    private String outTransId;

    // 第三方订单ID
    private String partyOrderId;

    // 支付金额
    private String payAmt;

    // 支付场景
    private String payScene;

    // POSP流水号
    private String pospSeqId;

    // 产品ID
    private String productId;

    // 参考号
    private String refNo;

    // 请求日期
    private String reqDate;

    // 请求序列号
    @JSONField(name = "req_seq_id")
    private String reqSeqId;

    // 响应代码
    private String respCode;

    // 响应描述
    private String respDesc;

    // 风险检查数据
    private String riskCheckData;

    // 风险检查信息
    private String riskCheckInfo;

    // 结算金额
    private String settlementAmt;

    // 子响应代码
    private String subRespCode;

    // 子响应描述
    private String subRespDesc;

    // 补贴状态
    private String subsidyStat;

    // 系统ID
    private String sysId;

    // 交易类型
    private String tradeType;

    // 交易金额
    private String transAmt;

    // 交易日期
    private String transDate;

    // 交易费用津贴信息
    private String transFeeAllowanceInfo;

    // 交易状态
    private String transStat;

    // 交易时间
    private String transTime;

    // 交易类型
    private String transType;

    // 微信响应信息
    private String wxResponse;

}
