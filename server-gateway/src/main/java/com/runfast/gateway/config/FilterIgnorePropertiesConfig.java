package com.runfast.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 不需要验证token的url
 *
 * @author luojianbo
 * @date 2019/07/16
 */
@Component
@ConfigurationProperties(prefix="ignore")
@Data
public class FilterIgnorePropertiesConfig {
    private  List<String> urls = new ArrayList<>();

}
