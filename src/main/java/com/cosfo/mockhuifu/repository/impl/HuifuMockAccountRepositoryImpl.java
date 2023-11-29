package com.cosfo.mockhuifu.repository.impl;

import com.cosfo.mockhuifu.model.po.HuiFuMockAccount;
import com.cosfo.mockhuifu.mapper.HuifuMockAccountMapper;
import com.cosfo.mockhuifu.repository.HuifuMockAccountRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 汇付模拟账户 服务实现类
 * </p>
 *
 * @author George
 * @since 2023-11-29
 */
@Service
public class HuifuMockAccountRepositoryImpl extends ServiceImpl<HuifuMockAccountMapper, HuiFuMockAccount> implements HuifuMockAccountRepository {

}
