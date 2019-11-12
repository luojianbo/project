package com.runfast.gateway.service.impl;

import com.runfast.gateway.config.ClientKeyConfig;
import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.service.SignService;
import com.runfast.gateway.util.DateUtils;
import com.runfast.gateway.util.MD5SignUtils;
import com.runfast.gateway.util.RequestUtils;
import com.runfast.gateway.vo.RequestHeadVo;
import com.runfast.gateway.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  签名验证接口，验证签名sign的合法性
 *  请求数据格式有：
 *  1、application/json格式，请求数据包括：url地址中携带的key=value参数 + body中json格式数据
 *  2、application/x-www-form-urlencoded格式，请求数据包括：url地址中携带的key=value参数 + body中post的 key=value参数
 * @author luojianbo
 * @date 2019/6/5
 */
@Slf4j
public abstract class SignServiceImpl implements SignService {
    protected HttpHeaders header;
    protected MultiValueMap<String, String>  requestQueryParams;
    protected String requestUri;
    protected RequestHeadVo headVo;

    public SignServiceImpl(HttpHeaders header, MultiValueMap<String, String> requestQueryParams, String requestUri) {
        this.header = header;
        this.requestQueryParams = requestQueryParams;
        this.requestUri = requestUri;
    }

    @Override
    public ResultVo check() {
        if(requestUri.indexOf("/nsign") != -1){
            log.info("path={},无需Authorization认证", requestUri);
            return ResultVo.ok(ResultCodeEnum.SUCCESS.getDescription());
        }

        headVo = RequestUtils.getRequestHeadVo(header);
        ResultVo rs = checkVo(headVo);
        if(rs != null) {
            return rs;
        }

        rs = checkSign();
        if(rs != null) {
            return rs;
        }

        return ResultVo.ok(ResultCodeEnum.SUCCESS.getDescription());
    }


    protected ResultVo checkVo(RequestHeadVo headVo){
        //验证clientId
        if(StringUtils.isEmpty(headVo.getClientId()) || StringUtils.isEmpty(ClientKeyConfig.clientKey.get(headVo.getClientId()))){
            return ResultVo.fail(ResultCodeEnum.CLIENTID_ERROR);
        }

        //验证时间戳
        Long s = DateUtils.compareToSecond(headVo.getTimestamp());
        //log.info("Long s = "+s);
        if(s == null){
            return ResultVo.fail(ResultCodeEnum.TIMESTAMP_ERROR);
        }
        if(s.longValue() >= 300 || s.longValue() <-60){
            //时间戳和当前时间相比，大于300秒的，请求过期
            return ResultVo.fail(ResultCodeEnum.TIMESTAMP_ERROR);
        }

        //验证sign
        if(StringUtils.isEmpty(headVo.getSign())){
            return ResultVo.fail(ResultCodeEnum.SIGN_ERROR);
        }

        return null;
    }

    /**
     * 抽象方法，签名验证，交给post/get子类实现
     * @return
     */
    protected abstract ResultVo checkSign();



}
