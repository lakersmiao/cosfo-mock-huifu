package com.cosfo.mockhuifu.model.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: George
 * @date: 2023-11-29
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuResponseDTO<T> {
    /**
     * 系统id
     */
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
