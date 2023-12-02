package com.cosfo.mockhuifu.service.impl;

import com.cosfo.mockhuifu.model.po.HuiFuMockAccount;
import com.cosfo.mockhuifu.repository.HuifuMockAccountRepository;
import com.cosfo.mockhuifu.service.HuiFuMockAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @description: 汇付模拟支付单业务层
 * @author: George
 * @date: 2023-11-29
 **/
@Service
public class HuiFuMockAccountServiceImpl implements HuiFuMockAccountService {

    @Resource
    private HuifuMockAccountRepository huifuMockAccountRepository;

    @Override
    public void initAccount(String huiFuId) {
        HuiFuMockAccount account = huifuMockAccountRepository.getById(huiFuId);
        if (account != null) {
            return;
        }
        account = HuiFuMockAccount.builder()
                .huiFuId(huiFuId)
                .basicAmt(BigDecimal.ZERO)
                .delayedAmt(BigDecimal.ZERO)
                .build();
        huifuMockAccountRepository.save(account);
    }
}
