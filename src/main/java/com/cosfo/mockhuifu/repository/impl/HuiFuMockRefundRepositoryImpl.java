package com.cosfo.mockhuifu.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cosfo.mockhuifu.mapper.HuifuMockPaymentMapper;
import com.cosfo.mockhuifu.model.po.HuiFuMockRefund;
import com.cosfo.mockhuifu.mapper.HuifuMockRefundMapper;
import com.cosfo.mockhuifu.repository.HuiFuMockRefundRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟退款表 服务实现类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Service
public class HuiFuMockRefundRepositoryImpl extends ServiceImpl<HuifuMockRefundMapper, HuiFuMockRefund> implements HuiFuMockRefundRepository {

    @Resource
    private HuifuMockRefundMapper huifuMockRefundMapper;;
    @Override
    public BigDecimal queryTotalRefundAmtByReqDateAndReqSeqId(String orgReqDate, String orgReqSeqId, String stat) {
        QueryWrapper<HuiFuMockRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("org_req_date", orgReqDate);
        queryWrapper.eq("org_req_seq_id", orgReqSeqId);
        queryWrapper.eq("trans_stat", stat);
        queryWrapper.select("IFNULL(SUM(refund_amt), 0) as refundAmt");
        HuiFuMockRefund huiFuMockRefund = huifuMockRefundMapper.selectOne(queryWrapper);
        return huiFuMockRefund.getRefundAmt();
    }
}
