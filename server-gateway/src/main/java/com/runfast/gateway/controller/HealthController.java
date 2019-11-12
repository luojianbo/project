package com.runfast.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.vo.ResultVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:检查是否在线
 * Created by zhaoshajin on 2019/4/23 11:10
 */
@RestController
public class HealthController {

    @RequestMapping("/health")
    public String health() {
        return "health...";
    }

    @RequestMapping("/check")
    public String check() {
        return "{\"success\":true,\"msg\":\"\"}";
    }

    @RequestMapping("/actuator/health")
    public String actuatorHealth() {

        return "health...";
    }
}
