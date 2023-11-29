package com.cosfo.mockhuifu.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 汇付模拟退款表
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Getter
@Setter
@TableName("hui_fu_mock_refund")
public class HuiFuMockRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求日期(yyyyMMdd)
     */
    @TableField("req_date")
    private String reqDate;

    /**
     * 请求序列号
     */
    @TableField("req_seq_id")
    private String reqSeqId;

    /**
     * 汇付id
     */
    @TableField("huifu_id")
    private String huiFuId;

    /**
     * 退款金额
     */
    @TableField("refund_amt")
    private BigDecimal refundAmt;

    /**
     * 原交易请求日期(yyyyMMdd)
     */
    @TableField("org_req_date")
    private String orgReqDate;

    /**
     * 原交易请求序列id
     */
    @TableField("org_req_seq_id")
    private String orgReqSeqId;

    /**
     * create time
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * update time
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
