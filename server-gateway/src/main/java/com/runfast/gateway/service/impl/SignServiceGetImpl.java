package com.runfast.gateway.service.impl;

import com.runfast.gateway.config.ClientKeyConfig;
import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.util.MD5SignUtils;
import com.runfast.gateway.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  签名验证接口，验证签名sign的合法性
 *  请求数据格式有：
 *  get请求数据包括：url地址中携带的key=value参数
 * @author luojianbo
 * @date 2019/6/5
 */
@Slf4j
public class SignServiceGetImpl extends SignServiceImpl{
    public SignServiceGetImpl(HttpHeaders header, MultiValueMap<String, String> requestQueryParams, String requestUri) {
        super(header, requestQueryParams, requestUri);
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
        //计算签名字符串
        String sign = MD5SignUtils.sign(map,headVo,"",ClientKeyConfig.clientKey.get(headVo.getClientId()));
        if(!sign.equals(headVo.getSign())){
            log.error("sign error [request_sign={},server_sing={}]",headVo.getSign(),sign);
            //签名不一致
            return ResultVo.fail(ResultCodeEnum.SIGN_ERROR);
        }
        return null;
    }
}
