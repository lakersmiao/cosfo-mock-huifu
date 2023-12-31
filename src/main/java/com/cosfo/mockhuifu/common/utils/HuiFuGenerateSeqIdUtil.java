package com.cosfo.mockhuifu.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.cosfo.mockhuifu.common.constant.HuiFuConstant;

/**
 * @description: 汇付生成请求序列号id工具类
 * @author: George
 * @date: 2023-11-29
 **/
public class HuiFuGenerateSeqIdUtil {

    public static String generateHfSeqId(String prefix) {
        String hfJsapiSeqIdPrefix = HuiFuConstant.HF_JSAPI_SEQ_ID_PREFIX;
        String randomString = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER, 33);
        return hfJsapiSeqIdPrefix + randomString;
    }

    public static String generateRefundHfSeqId() {
        return generateHfSeqId(HuiFuConstant.HF_REFUND_SEQ_ID_PREFIX);
    }

    public static String generateJsapiHfSeqId() {
        return generateHfSeqId(HuiFuConstant.HF_JSAPI_SEQ_ID_PREFIX);
    }

}
