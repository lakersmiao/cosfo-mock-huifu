package com.cosfo.mockhuifu.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cosfo.mockhuifu.common.exception.ParamsException;
import com.cosfo.mockhuifu.model.po.HuiFuMockPayment;
import com.cosfo.mockhuifu.mapper.HuifuMockPaymentMapper;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟支付单 服务实现类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Service
public class HuifuMockPaymentRepositoryImpl extends ServiceImpl<HuifuMockPaymentMapper, HuiFuMockPayment> implements HuifuMockPaymentRepository {

    @Resource
    private HuifuMockPaymentMapper huifuMockPaymentMapper;

    @Override
    public int updateStatusById(Long id, String orgStat, String finalStat) {
        return huifuMockPaymentMapper.updateStatusById(id, orgStat, finalStat);
    }

    @Override
    public HuiFuMockPayment queryByReqDateAndReqSeqId(String orgReqDate, String orgReqSeqId, String stat) {
        if (orgReqDate == null || orgReqSeqId == null) {
            throw new ParamsException("请求日期和请求流水号不能为空");
        }
        LambdaQueryWrapper<HuiFuMockPayment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HuiFuMockPayment::getReqDate, orgReqDate);
        queryWrapper.eq(HuiFuMockPayment::getReqSeqId, orgReqSeqId);
        queryWrapper.eq(HuiFuMockPayment::getTransStat, stat);
        return huifuMockPaymentMapper.selectOne(queryWrapper);
    }
}
