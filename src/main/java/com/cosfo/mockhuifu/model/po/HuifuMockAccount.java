package com.cosfo.mockhuifu.model.po;

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
 * 汇付模拟账户
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Getter
@Setter
@TableName("huifu_mock_account")
public class HuifuMockAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 汇付id
     */
    @TableId("huifu_id")
    private String huifuId;

    /**
     * 基本户金额
     */
    @TableField("basic_amt")
    private BigDecimal basicAmt;

    /**
     * 延迟户金额
     */
    @TableField("delayed_amt")
    private BigDecimal delayedAmt;

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
