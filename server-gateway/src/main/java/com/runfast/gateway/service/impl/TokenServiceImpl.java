package com.runfast.gateway.service.impl;

import com.runfast.gateway.config.ClientKeyConfig;
import com.runfast.gateway.config.FilterIgnorePropertiesConfig;
import com.runfast.gateway.enums.ResultCodeEnum;
import com.runfast.gateway.service.TokenService;
import com.runfast.gateway.util.SpringUtils;
import com.runfast.gateway.util.TokenUtils;
import com.runfast.gateway.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;

/**
 *  token验证接口，验证token的合法性
 * @author luojianbo
 * @date 2019/6/5
 */
@Slf4j
public  class TokenServiceImpl implements TokenService {

    private HttpHeaders header;
    private String requestUri;
    //private static FilterIgnorePropertiesConfig filterIgnorePropertiesConfig = (FilterIgnorePropertiesConfig)SpringUtils.getBean("filterIgnorePropertiesConfig");
    public TokenServiceImpl(HttpHeaders header, String requestUri) {
        this.header = header;
        this.requestUri = requestUri;
    }

    @Override
    public ResultVo check() {
        if(requestUri.indexOf("/noken") != -1){
            log.info("path={},无需Authorization认证", requestUri);
            return ResultVo.ok(ResultCodeEnum.SUCCESS.getDescription());
        }
        //无需认证（包括basic认证的接口）
        //List<String> urls = filterIgnorePropertiesConfig.getUrls();
        if(ClientKeyConfig.nokenUrl != null && ClientKeyConfig.nokenUrl.size() >0 ) {//有配置验证地址
            for (Map.Entry<String, String> entry : ClientKeyConfig.nokenUrl.entrySet()) {
                String url = entry.getValue();
                if (url.endsWith("/*")) {
                    if (requestUri.indexOf(url.replaceAll("\\/\\*", "")) != -1) {
                        log.info("path={},无需Authorization认证", requestUri);
                        return ResultVo.ok(ResultCodeEnum.SUCCESS.getDescription());
                    }
                } else if (requestUri.equals(url)) {
                    log.info("path={},无需Authorization认证", requestUri);
                    return ResultVo.ok(ResultCodeEnum.SUCCESS.getDescription());
                }
            }
        }
        //token为空
        if(StringUtils.isEmpty(header.getFirst(TokenUtils.TOKEN))){
            return ResultVo.fail(ResultCodeEnum.ACCECT_TOKEN_ERROR);
        }
//        boolean boo = TokenUtils.check(header.getFirst(TokenUtils.TOKEN));
//        if(!boo){
//            return ResultVo.fail(ResultCodeEnum.ACCECT_TOKEN_ERROR);
//        }
        return ResultVo.ok(ResultCodeEnum.SUCCESS.getDescription());
    }
}
