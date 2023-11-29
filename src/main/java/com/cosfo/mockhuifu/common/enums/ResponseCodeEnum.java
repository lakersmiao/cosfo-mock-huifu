package com.cosfo.mockhuifu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 返回码枚举
 * @author: George
 * @date: 2023-11-29
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {

    /**
     * jsapi模拟下单成功
     */
    MOCK_JSAPI_SUCCESS("00000100", "模拟下单成功"),
    ;

    /**
     * code
     */
    private String code;

    /**
     * 描述
     */
    private String desc;
}
