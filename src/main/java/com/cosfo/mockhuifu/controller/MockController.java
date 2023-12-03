package com.cosfo.mockhuifu.controller;

import cn.hutool.json.JSONObject;
import com.alibaba.druid.mock.MockRef;
import com.alibaba.fastjson.JSON;
import com.cosfo.mockhuifu.model.dto.request.HuiFuPayRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRefundRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuPayResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;
import com.cosfo.mockhuifu.service.MockRefundService;
import com.cosfo.mockhuifu.service.MockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 模拟汇付控制层
 * @author: George
 * @date: 2023-11-27
 **/

@RestController
@RequestMapping("/mock-huifu")
@Slf4j
public class MockController {

    @Resource
    private MockService mockService;
    @Resource
    private MockRefundService mockRefundService;

    /**
     * 模拟汇付支付接口
     */
    @PostMapping("/v2/trade/payment/jspay")
    public String mockJsApi(@RequestBody HuiFuRequestDTO<HuiFuPayRequestDTO> payRequestDTO){
        HuiFuResponseDTO huiFuResponseDTO = mockService.mockJsApi(payRequestDTO);
        return JSON.toJSONString(huiFuResponseDTO);
    }

    /**
     * 模拟汇付退款接口
     */
    @PostMapping("v2/trade/payment/scanpay/refund")
    public String mockRefund(@RequestBody HuiFuRequestDTO<HuiFuRefundRequestDTO> refundRequestDTO){
        // traceId:ea1af40ad217015836205072703d0001
        HuiFuResponseDTO huiFuResponseDTO = mockRefundService.mockRefund(refundRequestDTO);
        return JSON.toJSONString(huiFuResponseDTO);
    }
}
