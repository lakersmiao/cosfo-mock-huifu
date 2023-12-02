package com.cosfo.mockhuifu.model.dto.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 汇付基础响应DTO
 * @author: George
 * @date: 2023-12-01
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuiFuBaseResponseDTO {

    /**
     * 响应码
     */
    @JSONField(name = "resp_code")
    private String respCode;

    /**
     * 签名
     */
    private String sign;

    /**
     * 响应数据
     */
    @JSONField(name = "resp_data")
    private String respData;

    /**
     * 响应描述
     */
    @JSONField(name = "resp_desc")
    private String respDesc;
}
