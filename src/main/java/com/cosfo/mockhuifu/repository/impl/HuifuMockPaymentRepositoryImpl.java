package com.cosfo.mockhuifu.repository.impl;

import com.cosfo.mockhuifu.model.po.HuifuMockPayment;
import com.cosfo.mockhuifu.mapper.HuifuMockPaymentMapper;
import com.cosfo.mockhuifu.repository.HuifuMockPaymentRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 汇付模拟支付单 服务实现类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Service
public class HuifuMockPaymentRepositoryImpl extends ServiceImpl<HuifuMockPaymentMapper, HuifuMockPayment> implements HuifuMockPaymentRepository {

    @Resource
    private HuifuMockPaymentMapper huifuMockPaymentMapper;

    @Override
    public int updateStatusById(Long id, String orgStat, String finalStat) {
        return huifuMockPaymentMapper.updateStatusById(id, orgStat, finalStat);
    }
}
