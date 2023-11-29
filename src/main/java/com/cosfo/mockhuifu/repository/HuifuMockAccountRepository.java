package com.cosfo.mockhuifu.repository;

import com.cosfo.mockhuifu.model.po.HuiFuMockAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 汇付模拟账户 服务类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
public interface HuifuMockAccountRepository extends IService<HuiFuMockAccount> {

    /**
     * 更新延迟金额
     * @param huiFuId
     * @param increaseDelayedAmt
     */
    int increaseDelayedAmt(String huiFuId, BigDecimal increaseDelayedAmt);
}
