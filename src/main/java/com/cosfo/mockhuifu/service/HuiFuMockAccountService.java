package com.cosfo.mockhuifu.service;

/**
 * @description: 汇付模拟支付单业务层
 * @author: George
 * @date: 2023-11-29
 **/
public interface HuiFuMockAccountService {


    /**
     * 初始化账户
     *
     * @param huiFuId 汇付id
     */
    void initAccount(String huiFuId);
}
