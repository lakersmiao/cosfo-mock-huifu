package com.cosfo.mockhuifu.mapper;

import com.cosfo.mockhuifu.model.po.HuiFuMockTransactionSummary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟交易汇总 Mapper 接口
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Mapper
public interface HuiFuMockTransactionSummaryMapper extends BaseMapper<HuiFuMockTransactionSummary> {

    /**
     * 更新延迟户金额
     * @param reqDate
     * @param reqSeqId
     * @param amt
     * @return
     */
    int increaseDelayedAmt(@Param("reqDate") String reqDate, @Param("reqSeqId") String reqSeqId, @Param("amt") BigDecimal amt);
}
