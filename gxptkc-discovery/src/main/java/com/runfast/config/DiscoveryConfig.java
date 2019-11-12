package com.runfast.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.runfast.discovery.MyDiscoveryEnabledStrategy;


import com.runfast.feign.FeignLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import feign.Logger;
import feign.RequestTemplate;
import feign.RequestInterceptor;


import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
/**
 * @Description
 * @Author luojianbo
 * @Date2019/11/1 15:21
 **/
@Configuration
public class DiscoveryConfig implements RequestInterceptor {
    @Value(value = "${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    @Bean
    public ConfigService configService() {
        try {
            return NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }
    //    // 自定义负载均衡的灰度策略
    @Bean
    public MyDiscoveryEnabledStrategy myDiscoveryEnabledStrategy() {
        return new MyDiscoveryEnabledStrategy();
    }


    @Bean
    Logger.Level feignLoggerLevel() {
        //这里记录所有，根据实际情况选择合适的日志level
        return Logger.Level.FULL;
    }
    @Bean
    Logger feignLogger(){
        return new FeignLogger();
    }

    public void apply(RequestTemplate template){
        try {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
            if(attributes != null) {

                    HttpServletRequest request = attributes.getRequest();
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if (headerNames != null) {
                        while (headerNames.hasMoreElements()) {
                            String name = headerNames.nextElement();
                            if ("content-length".equals(name)) {
                                continue;
                            }
                            String values = request.getHeader(name);
                            template.header(name, values);

                        }
                    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
