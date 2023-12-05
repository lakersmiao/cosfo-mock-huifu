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
 * 汇付模拟交易汇总
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Getter
@Setter
@TableName("hui_fu_mock_transaction_summary")
public class HuiFuMockTransactionSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原交易请求序列号id
     */
    @TableField("org_req_seq_id")
    private String orgReqSeqId;

    /**
     * 原交易请求日期
     */
    @TableField("org_req_date")
    private String orgReqDate;

    /**
     * 原始金额
     */
    @TableField("org_amt")
    private BigDecimal orgAmt;

    /**
     * 延迟金额
     */
    @TableField("delayed_amt")
    private BigDecimal delayedAmt;

    /**
     * 确认金额
     */
    @TableField("confirmed_amt")
    private BigDecimal confirmedAmt;

    /**
     * 退款金额
     */
    @TableField("refund_amt")
    private BigDecimal refundAmt;

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
