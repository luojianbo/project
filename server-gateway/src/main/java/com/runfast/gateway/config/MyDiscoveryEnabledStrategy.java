package com.runfast.gateway.config;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 *
 * [
 * 	{
 * 	    "service": "**:pre,**:**.pre",
 * 	    "clientVersion": "**-pre,**-**.pre,**-**.pre2,200001-2019.12.1,200005-19.12.0"
 * 	},
 * 	{
 * 	    "service": "**:**.yufabu",
 * 	    "clientVersion": "**-**.newtest"
 * 	},
 * 	{
 * 	    "service": "**:**.nwtest",
 * 	    "clientVersion": "**-**.nwtest"
 * 	}
 * ]
 */

import com.nepxion.discovery.plugin.strategy.matcher.DiscoveryMatcherStrategy;
import com.runfast.gateway.util.IpUtil;
import com.runfast.gateway.vo.GrayscaleInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import com.nepxion.discovery.plugin.strategy.adapter.DiscoveryEnabledStrategy;
import com.nepxion.discovery.plugin.strategy.gateway.context.GatewayStrategyContextHolder;
import com.netflix.loadbalancer.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

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

    private AntPathMatcher matcher = new AntPathMatcher();
    public static Map<String,String> serviceMap = new HashMap<>();//灰度服务列表
    public static Map<String,String> clientVersionMap = new HashMap<>();//灰度前端版本
    public static Map<String,String> remoteIpMap = new HashMap<>();//灰度前端ip
    public static boolean isOpen = false;//是否开启灰度
    public static final  String SERVICE_STR=":";//灰度服务名称和版本号分隔符
    public static final  String VERSION_STR="-";//灰度客户端编号和版本号分隔符
    public static final  String END_STR=",";//结束符



    @Autowired
    private GatewayStrategyContextHolder gatewayStrategyContextHolder;

    @Autowired
    private PluginAdapter pluginAdapter;

    @Override
    public boolean apply(Server server) {
        // 对Rest调用传来的Header参数（例如：mobile）做策略
        try {
            return applyFromHeader(server);
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    // 根据REST调用传来的Header参数（例如：mobile），选取执行调用请求的服务实例
    private boolean applyFromHeader(Server server) {
        if(!isOpen){
            return true;
        }
        //String clientVersion = gatewayStrategyContextHolder.getHeader("clientVersion");
        String clientVersion = getClientVersion();
        if (StringUtils.isNotEmpty(clientVersion)) {
            clientVersion = clientVersion.toLowerCase();
        }

        String serviceId = pluginAdapter.getServerServiceId(server);
        String version = pluginAdapter.getServerVersion(server);
        String address = server.getHostPort();
        String servStr = (serviceId+SERVICE_STR+version).toLowerCase();
        String ver = serviceCheck(servStr);
        if(ver != null){//有匹配版本号规则
            if(versionCheck(clientVersion,ver)){
                log.info("负载均衡用户定制触发clientVersion灰度：clientVersion={}, serviceId={}:{},  address={}", clientVersion,  serviceId, version, address);
                return true;
            }else{
                return false;
            }
        }else{//无版本号规则
            if(versionCheck(clientVersion)){

                return false;
            }else{
                log.info("负载均衡用户定制触发clientVersion灰度：clientVersion={}, serviceId={}:{},  address={}", clientVersion,  serviceId, version, address);
                return true;
            }
        }
    }




    /**
     *功能描述  判断请求版本号是否在灰度规则
     * @param clientVersion
    * @param servStr
     * @return boolean
     * @author luojianbo
     * @date 2020/1/9 17:03
     */
    private boolean versionCheck(String clientVersion,String ver){
        String[] vers= ver.split(",");
        for(String info : vers){
            if(matcher.match(info,clientVersion)){//通配符规则匹配
                //log.info("clientVersion={},versionInfo={},true",clientVersion,info);
                return true;
            }
            //log.info("clientVersion={},versionInfo={},false",clientVersion,info);
        }
        return false;
    }

    private boolean versionCheck(String clientVersion){
        for (Map.Entry<String, String> entry : clientVersionMap.entrySet()) {

            if(matcher.match(entry.getKey(),clientVersion)){//通配符规则匹配
                //log.info("clientVersion={},versionKey={},true",clientVersion,entry.getKey());
                return true;
            }
            //log.info("clientVersion={},versionKey={},false",clientVersion,entry.getKey());
        }
        return false;
    }



    /**
     *功能描述  判断当前服务是否在灰度规则
     * @param servStr
     * @author luojianbo
     * @date 2020/1/9 17:03
     */
    private String serviceCheck(String servStr){
        String str = null;
        for (Map.Entry<String, String> entry : serviceMap.entrySet()) {

            if(matcher.match(entry.getKey(),servStr)){
                //log.info("servStr={},serviceKey={},true",servStr,entry.getKey());
                str = entry.getValue();
                break;
            }
            //log.info("servStr={},serviceKey={},false",servStr,entry.getKey());
        }
        return str;
    }


    private String getClientVersion(){
        return gatewayStrategyContextHolder.getHeader("clientId")+VERSION_STR+gatewayStrategyContextHolder.getHeader("clientVersion");
    }



}