package com.cosfo.mockhuifu.model.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 汇付退款请求DTO
 * @author: George
 * @date: 2023-12-03
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuRefundRequestDTO {

    /**
     * 汇付id
     */
    @JSONField(name = "huifu_id")
    private String huiFuId;

    /**
     * 回调地址
     */
    @JSONField(name = "notify_url")
    private String notifyUrl;

    /**
     * 退款金额
     */
    @JSONField(name = "order_amt")
    private String orderAmt;

    /**
     * 原交易请求日期
     */
    @JSONField(name = "org_req_date")
    private String orgReqDate;

    /**
     * 原交易请求流水号
     */
    @JSONField(name = "org_req_seq_id")
    private String orgReqSeqId;

    /**
     * 原交易全局流水号
     */
    @JSONField(name = "org_hf_seq_id")
    private String orgHfSeqId;

    /**
     * 退款请求日期
     */
    @JSONField(name = "req_date")
    private String reqDate;

    /**
     * 退款请求流水号
     */
    @JSONField(name = "req_seq_id")
    private String reqSeqId;

    /**
     * 全局流水号
     */
    @JSONField(name = "hf_seq_id")
    private String hfSeqId;
}
