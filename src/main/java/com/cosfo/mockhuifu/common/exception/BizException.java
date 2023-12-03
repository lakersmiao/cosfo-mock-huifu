package com.cosfo.mockhuifu.common.exception;

/**
 * @description: 参数异常
 * @author: George
 * @date: 2023-12-03
 **/
public class BizException extends RuntimeException{

        public BizException(String message) {
            super(message);
        }
}
