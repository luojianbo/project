package com.runfast;


import com.runfast.gateway.config.MyDiscoveryEnabledStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableDiscoveryClient
public class AppGateway  {
    public static void main(String[] args) {
        SpringApplication.run(AppGateway.class, args);
    }
//    // 自定义负载均衡的灰度策略
    @Bean
    public MyDiscoveryEnabledStrategy myDiscoveryEnabledStrategy() {
        return new MyDiscoveryEnabledStrategy();
    }



}