package com.runfast.gateway.filter;


import com.alibaba.fastjson.JSONObject;
import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.service.SignService;
import com.runfast.gateway.service.impl.SignServiceGetImpl;
import com.runfast.gateway.service.impl.SignServicePostImpl;
import com.runfast.gateway.vo.ResultVo;
import com.runfast.gateway.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 * 签名sign前置过滤器，验证sign的合法性
 *
 * @author luojianbo
 * @date 2019/6/5
 */
@Component
@Slf4j
public class AuthSignFilter implements GlobalFilter, Ordered {


    @Autowired
    private ResponseUtils responseUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        String requestUri = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethodValue().toLowerCase();
        if ("post".equals(method)) {
            //post方法要从mono中取出body数据
            Mono<Void> responseMono = serverRequest.bodyToMono(String.class)
                    .flatMap(body -> {
                        //检查签名是否正确,QueryParams为get请求参数,body为post类型请求参数
                        SignService signService = new SignServicePostImpl(exchange.getRequest().getHeaders(), exchange.getRequest().getQueryParams(), requestUri, body);
                        ResultVo rs = signService.check();
                        if (rs.isSuccess()) {//验证通过
                            return getVoidMono(exchange, chain, String.class, Mono.just(body));
                        } else {//验证不通过
                            log.error("AuthSignFilter error {}",JSONObject.toJSONString(rs));
                            return responseUtil.getResponse(HttpStatus.OK, exchange, rs);
                            //return getVoidMono(exchange, chain, String.class, Mono.just(body));
                        }
                    });
            return responseMono;
        }else if("get".equals(method)){
            //检查签名是否正确,QueryParams为get请求参数，get请求不需要验证body参数
            SignService signService = new SignServiceGetImpl(exchange.getRequest().getHeaders(), exchange.getRequest().getQueryParams(), requestUri);
            ResultVo rs = signService.check();
            if (rs.isSuccess()) {//验证通过
                return chain.filter(exchange);
            } else {//验证不通过
                log.error("AuthSignFilter error {}",JSONObject.toJSONString(rs));
                return responseUtil.getResponse(HttpStatus.OK, exchange, rs);
                //return chain.filter(exchange);
            }
        }
        return responseUtil.getResponse(HttpStatus.UNAUTHORIZED,exchange, ResultVo.fail(ResultCodeEnum.REQUEST_METHOD_ERROR));
    }

    /**
     * 参照 ModifyRequestBodyGatewayFilterFactory.java 截取的方法
     * @param exchange
     * @param chain
     * @param outClass
     * @param modifiedBody
     * @return
     */
    private Mono<Void> getVoidMono(ServerWebExchange exchange, GatewayFilterChain chain, Class outClass, Mono<?> modifiedBody) {

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        // the new content type will be computed by bodyInserter
        // and then set in the request decorator
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage,  new BodyInserterContext())
                // .log("modify_request", Level.INFO)
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                            exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            long contentLength = headers.getContentLength();
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(super.getHeaders());
                            if (contentLength > 0) {
                                httpHeaders.setContentLength(contentLength);
                            } else {
                                // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            }
                            return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }));
    }


    @Override
    public int getOrder() {
        return 0;
    }



}
