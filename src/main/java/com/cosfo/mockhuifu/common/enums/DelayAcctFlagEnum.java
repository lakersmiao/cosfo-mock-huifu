package com.cosfo.mockhuifu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 延迟交易枚举
 * @author: George
 * @date: 2023-11-29
 **/
@Getter
@AllArgsConstructor
public enum DelayAcctFlagEnum {

    DELAY("Y", "延时交易"),
    REAL_TIME("N", "实时交易"),
    ;

    /**
     * 标识
     */
    private String flag;

    /**
     * 描述
     */
    private String desc;
}
