package com.cosfo.mockhuifu.service;

import com.cosfo.mockhuifu.model.dto.request.HuiFuPayRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;

/**
 * @description: 模拟业务层
 * @author: George
 * @date: 2023-11-29
 **/
public interface MockService {

    /**
     * 模拟支付接口
     * @param payRequestDTO
     * @return
     */
    String mockJsApi(HuiFuRequestDTO<HuiFuPayRequestDTO> payRequestDTO);

}
