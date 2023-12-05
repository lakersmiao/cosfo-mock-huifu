package com.cosfo.mockhuifu.repository.impl;

import com.cosfo.mockhuifu.model.po.HuiFuMockTransactionSummary;
import com.cosfo.mockhuifu.mapper.HuiFuMockTransactionSummaryMapper;
import com.cosfo.mockhuifu.repository.HuiFuMockTransactionSummaryRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟交易汇总 服务实现类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Service
public class HuiFuMockTransactionSummaryRepositoryImpl extends ServiceImpl<HuiFuMockTransactionSummaryMapper, HuiFuMockTransactionSummary> implements HuiFuMockTransactionSummaryRepository {

    @Resource
    private HuiFuMockTransactionSummaryMapper huiFuMockTransactionSummaryMapper;

    @Override
    public int increaseDelayedAmt(String reqDate, String reqSeqId, BigDecimal amt) {
        return huiFuMockTransactionSummaryMapper.increaseDelayedAmt(reqDate, reqSeqId, amt);
    }
}
