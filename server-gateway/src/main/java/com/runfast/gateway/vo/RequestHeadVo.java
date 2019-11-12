package com.runfast.gateway.vo;


import com.runfast.gateway.util.RequestUtils;

/**
 * Description:请求头部参数vo
 * Created by luojianbo on 2019/7/27 10:47
 */
public class RequestHeadVo {
    //客户端编码
    private String clientId;
    //时间戳
    private String timestamp;
    //请求体通过md5算法生成的签名
    private String sign;
    //请求令牌
    private String token;
    //API接口版本号
    private String apiVersion;
    //前端版本号
    private String clientVersion;

    public RequestHeadVo(String clientId, String timestamp, String sign, String token) {
        this.clientId = RequestUtils.getNullStr(clientId);
        this.timestamp = RequestUtils.getNullStr(timestamp);
        this.sign = RequestUtils.getNullStr(sign);
        this.token = RequestUtils.getNullStr(token);
    }

    public RequestHeadVo(String clientId, String timestamp, String sign, String token, String apiVersion, String clientVersion) {
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.sign = sign;
        this.token = token;
        this.apiVersion = apiVersion;
        this.clientVersion = clientVersion;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}
