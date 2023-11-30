package com.cosfo.mockhuifu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: George
 * @date: 2023-11-30
 **/
@Getter
@AllArgsConstructor
public enum FeeFlagEnum {

    INNER_BUCKLE("1", "内扣"),
    OUTER_BUCKLE("2", "外扣"),
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
