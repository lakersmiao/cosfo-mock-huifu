package com.cosfo.mockhuifu.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 汇付模拟支付单
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("hui_fu_mock_payment")
public class HuiFuMockPayment implements Serializable {

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
     * 请求序列id
     */
    @TableField("req_seq_id")
    private String reqSeqId;

    /**
     * P、处理中 S、成功 F、失败
     */
    @TableField("trans_stat")
    private String transStat;

    /**
     * 汇付id
     */
    @TableField("huifu_id")
    private String huiFuId;

    /**
     * 支付类型 T_JSAPI: 微信公众号
T_MINIAPP: 微信小程序
A_JSAPI: 支付宝JS
A_NATIVE: 支付宝正扫
U_NATIVE: 银联正扫
U_JSAPI: 银联JS
D_NATIVE: 数字人民币正扫
T_H5：微信直连H5支付
T_APP：微信APP支付（只支持直连）
T_NATIVE：微信正扫（只支持直连）
     */
    @TableField("trade_type")
    private String tradeType;

    /**
     * 交易金额
     */
    @TableField("trans_amt")
    private BigDecimal transAmt;

    /**
     * 过期时间
     */
    @TableField("time_expire")
    private String timeExpire;

    /**
     * Y、延迟交易 N：非延迟交易
     */
    @TableField("delay_acct_flag")
    private String delayAcctFlag;

    /**
     * 回调地址
     */
    @TableField("notify_url")
    private String notifyUrl;

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
