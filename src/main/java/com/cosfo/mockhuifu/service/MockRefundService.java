package com.cosfo.mockhuifu.service;

import com.cosfo.mockhuifu.model.dto.request.HuiFuRefundRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;

/**
 * @description: 模拟退款业务层接口
 * @author: George
 * @date: 2023-12-03
 **/
public interface MockRefundService {

    /**
     * 模拟退款接口
     * @param refundRequestDTO
     * @return
     */
    HuiFuResponseDTO mockRefund(HuiFuRequestDTO<HuiFuRefundRequestDTO> refundRequestDTO);
}
