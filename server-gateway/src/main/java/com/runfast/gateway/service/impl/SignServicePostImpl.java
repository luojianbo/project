package com.runfast.gateway.service.impl;

import com.runfast.gateway.config.ClientKeyConfig;
import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.util.MD5SignUtils;
import com.runfast.gateway.util.RequestUtils;
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
 *  post请求数据格式有：
 *  1、application/json格式，请求数据包括：url地址中携带的key=value参数 + body中json格式数据
 *  2、application/x-www-form-urlencoded格式，请求数据包括：url地址中携带的key=value参数 + body中post的 key=value参数
 * @author luojianbo
 * @date 2019/6/5
 */
@Slf4j
public class SignServicePostImpl extends SignServiceImpl{
    private String body;
    /**
     *
     * @param header 请求头
     * @param requestQueryParams url地址后面的查询参数
     * @param requestUri 请求地址
     * @param body 请求body参数
     */
    public SignServicePostImpl(HttpHeaders header, MultiValueMap<String, String> requestQueryParams, String requestUri, String body) {
        super(header, requestQueryParams, requestUri);
        this.body = body;
    }

    /**
     * 签名验证逻辑
     * @return
     */
    @Override
    protected ResultVo checkSign() {
        //取出地址中携带参数的值
        Map<String,String> map = new HashMap<>();
        if(requestQueryParams != null && requestQueryParams.size() >0) {
            for (Map.Entry<String, List<String>> entry : requestQueryParams.entrySet()) {
                map.put(entry.getKey(),StringUtils.join(entry.getValue(), ""));
            }
        }
        MediaType mediaType = header.getContentType();
        String sign = null;
        if(MediaType.APPLICATION_JSON.equals(mediaType) || MediaType.TEXT_PLAIN.equals(mediaType)){
            //计算签名字符串
            sign = MD5SignUtils.sign(map,headVo,body,ClientKeyConfig.clientKey.get(headVo.getClientId()));
            if(!sign.equals(headVo.getSign())){
                log.error("sign error [request_sign={},server_sing={}]",headVo.getSign(),sign);
                //签名不一致
                return ResultVo.fail(ResultCodeEnum.SIGN_ERROR);
            }
        }else if(MediaType.APPLICATION_FORM_URLENCODED.equals(mediaType)){//其他类型body为空
            map.putAll(RequestUtils.getMapData(body));
            //计算签名字符串
            sign = MD5SignUtils.sign(map,headVo,"",ClientKeyConfig.clientKey.get(headVo.getClientId()));
            if(!sign.equals(headVo.getSign())){
                log.error("sign error [request_sign={},server_sing={}]",headVo.getSign(),sign);
                //签名不一致
                return ResultVo.fail(ResultCodeEnum.SIGN_ERROR);
            }
        }
        return null;
    }
}
