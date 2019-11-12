package com.runfast.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @Description
 * @Author luojianbo
 * @Date2019/10/30 17:24
 **/
public class IpUtil {
    /**
     *功能描述 获取真时ip
     * @param request
     * @return java.lang.String
     * @author luojianbo
     * @date 2019/10/30 17:28
     */
    public static String getIpAddr(ServerHttpRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeaders().getFirst("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }
}
