package com.cosfo.mockhuifu.repository;

import com.cosfo.mockhuifu.model.po.HuiFuMockTransactionSummary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟交易汇总 服务类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
public interface HuiFuMockTransactionSummaryRepository extends IService<HuiFuMockTransactionSummary> {

    /**
     * 更新延迟户金额
     * @param reqDate
     * @param reqSeqId
     * @param amt
     * @return
     */
    int increaseDelayedAmt(String reqDate, String reqSeqId, BigDecimal amt);
}
