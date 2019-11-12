package com.runfast.gateway.util;

import com.alibaba.fastjson.JSONObject;
import com.runfast.gateway.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Description:响应
 * Created by zhaoshajin on 2019/4/28 17:47
 */
@Component
@Slf4j
public class ResponseUtils {
    

    /**
     * 把dataResult放到response中返回
     */
    public Mono<Void> getResponse(HttpStatus httpStatus, ServerWebExchange exchange, ResultVo dataResult) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bits = JSONObject.toJSONString(dataResult).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(httpStatus);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

}
