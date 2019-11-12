package com.runfast.gateway.controller;

import com.alibaba.fastjson.JSONObject;

import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.vo.ResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:熔断处理
 * @author luojianbo
 * @date 2019/6/5
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public String health(){
        return JSONObject.toJSONString(ResultVo.fail(ResultCodeEnum.SYSTEM_BUSY));
    }

    @RequestMapping("/error")
    public String error(){
        return JSONObject.toJSONString(ResultVo.fail(ResultCodeEnum.SYSTEM_BUSY));
    }

}
