package com.cosfo.mockhuifu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 模拟汇付控制层
 * @author: George
 * @date: 2023-11-27
 **/

@RestController
@RequestMapping("/mock-huifu")
@Slf4j
public class MockController {

    /**
     * 模拟汇付支付接口
     */
    @PostMapping("/jsapi")
    public void mockJsApi(){
        log.info("接收到模拟支付请求，已成功支付");
    }

}
