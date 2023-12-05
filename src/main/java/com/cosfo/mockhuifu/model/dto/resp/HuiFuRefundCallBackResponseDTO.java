package com.cosfo.mockhuifu.model.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @description: 退款回调响应DTO
 * @author: George
 * @date: 2023-12-05
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuRefundCallBackResponseDTO {

    // 账户ID
    private String acctId;

    // 账户分组编号
    private String acctSplitBunch;

    // 实际退款金额
    private String actualRefAmt;

    // 银联子商户号
    private String atuSubMerId;

    // 授权号
    private String authNo;

    // 代理商ID
    private String bagentId;

    // 银行代码
    private String bankCode;

    // 银行描述
    private String bankDesc;

    // 银行消息
    private String bankMessage;

    // 银行流水号
    private String bankSeqId;

    // 银行类型
    private String bankType;

    // 基础账户ID
    private String baseAcctId;

    // 批次ID
    private String batchId;

    // 组合支付数据
    private String combinedpayData;

    // 组合支付手续费金额
    private String combinedpayFeeAmt;

    // 设备ID
    private String devsId;

    // 结束时间
    private String endTime;

    // 汇付流水号
    private String hfSeqId;

    // 汇付ID
    private String huifuId;

    // 商户名称
    private String merName;

    // 商户操作员ID
    private String merOperId;

    // 商户订单ID
    private String merOrdId;

    // 商户私有信息
    private String merPriv;

    // 美付通折扣
    private String mypaytsfDiscount;

    // 是否需要大对象
    private String needBigObject;

    // 订单金额
    private String ordAmt;

    // 订单ID
    private String orderId;

    // 原始津贴类型
    private String orgAllowanceType;

    // 原始手续费金额
    private String orgFeeAmt;

    // 原始手续费标志
    private String orgFeeFlag;

    // 原始手续费记录类型
    private String orgFeeRecType;

    // 原始订单金额
    private String orgOrdAmt;

    // 原始请求日期
    private String orgReqDate;

    // 原始请求流水号
    private String orgReqSeqId;

    // 原始终端订单ID
    private String orgTermOrdId;

    // 外部订单ID
    private String outOrdId;

    // 合作商订单ID
    private String partyOrderId;

    // 商品ID
    private String productId;

    // 津贴扣减
    private String refCut;

    // 参考号
    private String refNo;

    // 备注
    private String remark;

    // 请求日期
    private String reqDate;

    // 请求流水号
    private String reqSeqId;

    // 子响应码
    private String subRespCode;

    // 子响应描述
    private String subRespDesc;

    // 系统ID
    private String sysId;

    // 总退款金额
    private String totalRefAmt;

    // 总退款手续费金额
    private String totalRefFeeAmt;

    // 交易日期
    private String transDate;

    // 交易完成时间
    private String transFinishTime;

    // 交易状态
    private String transStat;

    // 交易时间
    private String transTime;

    // 交易类型
    private String transType;

}
