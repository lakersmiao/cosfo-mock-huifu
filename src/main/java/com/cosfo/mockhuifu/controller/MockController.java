package com.cosfo.mockhuifu.controller;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.cosfo.mockhuifu.model.dto.request.HuiFuPayRequestDTO;
import com.cosfo.mockhuifu.model.dto.request.HuiFuRequestDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuPayResponseDTO;
import com.cosfo.mockhuifu.model.dto.resp.HuiFuResponseDTO;
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

    /**
     * 模拟汇付支付接口
     */
    @PostMapping("/v2/trade/payment/jspay")
    public String mockJsApi(@RequestBody HuiFuRequestDTO<HuiFuPayRequestDTO> payRequestDTO){
        return mockService.mockJsApi(payRequestDTO);
//        log.info("接收到模拟支付请求，请求参数为：{}", payRequestDTO);
//        String response = "{\"data\":{\"bank_code\":\"SUCCESS\",\"bank_message\":\"成功\",\"delay_acct_flag\":\"Y\",\"hf_seq_id\":\"002900TOP3A231129125830P266ac139c9a00000\",\"huifu_id\":\"6666000133141030\",\"party_order_id\":\"03212311294671042816988\",\"pay_info\":\"{\\\"appId\\\":\\\"wxccce8cd0bed97cd1\\\",\\\"timeStamp\\\":\\\"1701233910\\\",\\\"nonceStr\\\":\\\"376f4d3f28514e47a8964cbe343147a5\\\",\\\"package\\\":\\\"prepay_id=wx291258306045424a06a0e843edd0290000\\\",\\\"signType\\\":\\\"RSA\\\",\\\"paySign\\\":\\\"LWloL0cPAnX8zqCW5ubPVzVe9ns3l6A3Iq6KaPkxkBhFqsfbAMq+gsXd19k3Y2g2Ktqokpu9krqtB/uTCtFmn/t1r5Oec31VztU9NoRfXsQlIXPeyiz5HEtNpmNFoKy0oTbydt+LoN7MZ6AninmYCafL6nBEWmMCzrexKuSy1sKZIfUr7CI9gPmNE612NhnOtu/h3WY1IisWL4O5Bp4DUk9zPKHmdxgRpzEhcq1YEh0UZzhz9ND6OgolCxbhE4K2NVmN1KhPOKNdFIFsXQPO1wg0B0zgkaiitC0OstSk+jZdaazZQfty5ikhu1435t5EVyMyQDbVKnsfyCt056ciww==\\\"}\",\"req_date\":\"20231129\",\"req_seq_id\":\"P1729726504479817728\",\"resp_code\":\"00000100\",\"resp_desc\":\"下单成功\",\"trade_type\":\"T_MINIAPP\",\"trans_amt\":\"8108.20\",\"trans_stat\":\"P\"},\"sign\":\"dZNyZQ4sO2mQEcIpzJIsnEOgtBlYy8/N94on3o9GTd+ySdudTEfG3i4jX6jvznNxJ5yfLWmvitrHZokEOG7At0aCI04+LCk2pv4hRNGKWl6hOTczEMEmMP2IFIBGfI2sj7PbVMt5GamGAehhCjCmgbF310y7UlgvmC5KJR0NDhwjwHiaRInVP+RML06tX60opj5SXXcjAtIIUVVLk7Vd03UMGSUYmkZVrlUXd34LGfax6onMIanzqSaglqtcpD9IILJj91NWHRw0l6owF2DdxEzfFb+otJNy4ZhFbnmYFHbhD+TmdZJ/7hbhSThC+jUgXjnrO12W2NstG/LPqdX2kg==\"}";
//        HuiFuResponseDTO<HuiFuPayResponseDTO> responseDTO = JSON.parseObject(response, HuiFuResponseDTO.class);
//        return JSON.toJSONString(responseDTO);
    }
}
