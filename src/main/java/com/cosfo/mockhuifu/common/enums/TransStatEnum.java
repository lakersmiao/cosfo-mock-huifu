package com.cosfo.mockhuifu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: George
 * @date: 2023-11-29
 **/
@Getter
@AllArgsConstructor
public enum TransStatEnum {

    PROCESSING("P", "处理中"),
    SUCCESS("S", "成功"),
    FAIL("F", "失败"),
    ;

    /**
     * 状态
     */
    private String stat;

    /**
     * 描述
     */
    private String desc;
}
