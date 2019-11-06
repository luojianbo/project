package com.runfast.discovery;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */


import com.nepxion.discovery.plugin.strategy.service.context.ServiceStrategyContextHolder;
import com.runfast.discovery.utils.IpUtils;
import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import com.nepxion.discovery.plugin.strategy.adapter.DiscoveryEnabledStrategy;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


/**
 *功能描述 实现了组合策略:前端版本号+前端请求真实IP 组合灰度
 * @param
 * @return
 * @author luojianbo
 * @date 2019/10/31 18:17
 */
@Slf4j
public class MyDiscoveryEnabledStrategy implements DiscoveryEnabledStrategy {


    public static Map<String,String> serviceMap = new HashMap<>();//灰度服务列表
    public static Map<String,String> clientVersionMap = new HashMap<>();//灰度前端版本
    public static Map<String,String> remoteIpMap = new HashMap<>();//灰度前端ip
    public static boolean isOpen = false;//是否开启灰度


    @Autowired
    private ServiceStrategyContextHolder serviceStrategyContextHolder;

    @Autowired
    private PluginAdapter pluginAdapter;

    @Override
    public boolean apply(Server server) {
        // 对Rest调用传来的Header参数（例如：mobile）做策略

        return applyFromHeader(server);

    }

    // 根据REST调用传来的Header参数（例如：mobile），选取执行调用请求的服务实例
    private boolean applyFromHeader(Server server) {
        if(isOpen == false){
            return true;
        }
        String clientVersion = serviceStrategyContextHolder.getHeader("clientVersion");
        String remoteIp= IpUtils.getIpAddr(serviceStrategyContextHolder.getRestAttributes().getRequest());
        String serviceId = pluginAdapter.getServerServiceId(server);
        String version = pluginAdapter.getServerVersion(server);
        String region = pluginAdapter.getServerRegion(server);
        String address = server.getHostPort();

        if(serviceMap.size() >0 && serviceMap.get(serviceId) != null){//有灰度服务

            if(clientVersionMap.size() >0 && remoteIpMap.size() >0){//版本号+远程ip灰度
                if(clientVersionMap.get(clientVersion) != null && ipCheck(remoteIp)){
                    if(StringUtils.equals(version, serviceMap.get(serviceId))){
                        log.info("负载均衡用户定制触发灰度：clientVersion={}, serviceId={}, version={}, region={}, remoteIp={}, address={}", clientVersion, serviceId, version, region, remoteIp,address);
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    if(StringUtils.equals(version, serviceMap.get(serviceId))){
                        return false;
                    }else{
                        return true;
                    }
                }
            }else if(clientVersionMap.size() >0 && remoteIpMap.size() ==0) {//版本号灰度
                if(clientVersionMap.get(clientVersion) != null){
                    if(StringUtils.equals(version, serviceMap.get(serviceId))){
                        log.info("负载均衡用户定制触发灰度：clientVersion={}, serviceId={}, version={}, region={}, remoteIp={}, address={}", clientVersion, serviceId, version, region, remoteIp,address);
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    if(StringUtils.equals(version, serviceMap.get(serviceId))){
                        return false;
                    }else{
                        return true;
                    }
                }
            } else if(clientVersionMap.size() ==0 && remoteIpMap.size() >0) {//版本号灰度
                if(ipCheck(remoteIp)){
                    if(StringUtils.equals(version, serviceMap.get(serviceId))){
                        log.info("负载均衡用户定制触发灰度：clientVersion={}, serviceId={}, version={}, region={}, remoteIp={}, address={}", clientVersion, serviceId, version, region, remoteIp,address);
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    if(StringUtils.equals(version, serviceMap.get(serviceId))){
                        return false;
                    }else{
                        return true;
                    }
                }
            }
        }

        return true;
    }


    private boolean ipCheck(String remoteIp){
        //检查是否包含ip
        if(StringUtils.isNotEmpty(remoteIp)) {
            for (Map.Entry<String, String> entry : remoteIpMap.entrySet()) {
                String ip = entry.getValue();
                if (remoteIp.startsWith(ip)) {
                    return true;
                }
            }
        }
        return false;
    }



}