package com.cosfo.mockhuifu;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: George
 * @date: 2023-11-27
 **/
@SpringBootApplication(scanBasePackages = {"com.cosfo"})
public class CosfoMockHuiFuApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(CosfoMockHuiFuApplication.class, args);
    }
}
