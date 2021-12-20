package com.cmiot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 协议网关服务入口类，使用SpringBoot开发
 */
@SpringBootApplication
@EnableScheduling
public class ProtocolGatewayService {

    public static void main(String[] args) {
        SpringApplication.run(ProtocolGatewayService.class, args);
    }
}

