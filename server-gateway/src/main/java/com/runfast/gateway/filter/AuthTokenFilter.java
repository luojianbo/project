package com.runfast.gateway.filter;


import com.alibaba.fastjson.JSONObject;
import com.runfast.gateway.service.TokenService;
import com.runfast.gateway.service.impl.TokenServiceImpl;
import com.runfast.gateway.vo.ResultVo;
import com.runfast.gateway.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * token前置过滤器，验证token的合法性
 *
 * @author luojianbo
 * @date 2019/6/5
 */
@Component //不需要验证token的地址太多，暂时放开token验证
@Slf4j
public class AuthTokenFilter implements GlobalFilter, Ordered {


    @Autowired
    private ResponseUtils responseUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders header = exchange.getRequest().getHeaders();
        String requestUri = exchange.getRequest().getPath().value();
        TokenService tokenService = new TokenServiceImpl(header,requestUri);
        ResultVo rs = tokenService.check();
        if (!rs.isSuccess()) {//验证不通过
            log.error("AuthTokenFilter error {}",JSONObject.toJSONString(rs));
            return responseUtil.getResponse(HttpStatus.UNAUTHORIZED, exchange, rs);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }



}
