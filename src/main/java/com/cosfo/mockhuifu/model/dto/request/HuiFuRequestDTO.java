package com.cosfo.mockhuifu.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: 汇付基础请求DTO
 * @author: George
 * @date: 2023-11-29
 **/
@Data
public class HuiFuRequestDTO<T> {

    /**
     * 系统id
     */
    @JsonProperty("sys_id")
    private String sysId;
    /**
     * 产品id
     */
    @JsonProperty("product_id")
    private String productId;
    /**
     * 签名
     */
    @JsonProperty("sign")
    private String sign;

    /**
     * 业务数据
     */
    @JsonProperty("data")
    private T data;
}
