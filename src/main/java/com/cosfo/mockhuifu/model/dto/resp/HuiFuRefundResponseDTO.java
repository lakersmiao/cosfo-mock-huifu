package com.cosfo.mockhuifu.model.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 退款响应对象DTO
 * @author: George
 * @date: 2023-12-03
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuRefundResponseDTO {

    @TableField("acct_split_bunch")
    private String acctSplitBunch;

    @TableField("bank_accepted_flag")
    private String bankAcceptedFlag;

    @TableField("bank_code")
    private String bankCode;

    @TableField("bank_message")
    private String bankMessage;

    @TableField("bank_seq_id")
    private String hfSeqId;

    @TableField("huifu_id")
    private String huiFuId;

    @TableField("ord_amt")
    private String ordAmt;

    @TableField("org_hf_seq_id")
    private String orgHfSeqId;

    @TableField("org_req_date")
    private String orgReqDate;

    @TableField("org_req_seq_id")
    private String orgReqSeqId;

    @TableField("product_id")
    private String productId;

    @TableField("remark")
    private String remark;

    @TableField("req_seq_id")
    private String reqSeqId;

    @TableField("req_date")
    private String reqDate;

    @TableField("resp_code")
    private String respCode;

    @TableField("resp_desc")
    private String respDesc;

    @TableField("trans_date")
    private String transDate;

    @TableField("trans_stat")
    private String transStat;

    @TableField("trans_time")
    private String transTime;
}
