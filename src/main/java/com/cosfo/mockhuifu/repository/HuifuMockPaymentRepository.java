package com.cosfo.mockhuifu.repository;

import com.cosfo.mockhuifu.model.po.HuifuMockPayment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 汇付模拟支付单 服务类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
public interface HuifuMockPaymentRepository extends IService<HuifuMockPayment> {

    /**
     * 根据id更新状态
     * @param paymentId
     * @param orgStat
     * @param finalStat
     * @return
     */
    int updateStatusById(Long paymentId, String orgStat, String finalStat);
}
