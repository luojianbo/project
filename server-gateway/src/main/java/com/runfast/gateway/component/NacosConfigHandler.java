package com.runfast.gateway.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.runfast.gateway.config.ClientKeyConfig;
import com.runfast.gateway.config.MyDiscoveryEnabledStrategy;
import com.runfast.gateway.route.DynamicRouteServiceImpl;
import com.runfast.gateway.vo.GrayscaleInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 监听Nacos Server下发的配置处理器
 * @author luojianbo
 * @date 2019/9/29 15:38
 */
@Component
@Slf4j
public class NacosConfigHandler {
    @Value(value = "${nacos.conf.gateway_conf}")
    private String gateway_conf;

    @Value(value = "${nacos.conf.clientId_conf}")
    private String clientId_conf;

    @Value(value = "${nacos.conf.nologin_conf}")
    private String nologin_conf;

    @Value(value = "${nacos.conf.token_url_conf}")
    private String token_url_conf;

    @Value(value = "${nacos.conf.discovery_conf}")
    private String discovery_conf;


    @Autowired
    private DynamicRouteServiceImpl dynamicRouteService;

    @Autowired
    private ConfigService configService;

    /**
     * 监听Nacos Server下发的动态路由配置
     * @PostConstruct   加上该注解表明该方法会在bean初始化后调用
     */
    @PostConstruct
    public void dynamicRouteByNacosListener() {
        try {
            String[] str = gateway_conf.split(":");
            String dataId = str[0];
            String group = str[1];
            String content = configService.getConfig(dataId, group, 5000);
            setGatewayConf(content);
            //监听配置中心配置数据变动
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    setGatewayConf(configInfo);
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
            //todo 提醒:异常自行处理此处省略
        }
    }

    /**
     * 监听Nacos Server下发的clientid配置
     * @PostConstruct   加上该注解表明该方法会在bean初始化后调用
     */
    @PostConstruct
    public void clientIdByNacosListener() {
        try {
            String[] str = clientId_conf.split(":");
            String dataId = str[0];
            String group = str[1];
            String content = configService.getConfig(dataId, group, 5000);
            setClientId(content);
            //监听配置中心配置数据变动
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    setClientId(configInfo);
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
            //todo 提醒:异常自行处理此处省略
        }
    }

    /**
     * 监听Nacos Server下发的clientid配置
     * @PostConstruct   加上该注解表明该方法会在bean初始化后调用
     */
    @PostConstruct
    public void nokenUrlByNacosListener() {
        try {
            String[] str = token_url_conf.split(":");
            String dataId = str[0];
            String group = str[1];
            String content = configService.getConfig(dataId, group, 5000);
            setTokenUrl(content);
            //监听配置中心配置数据变动
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    setTokenUrl(configInfo);
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
            //todo 提醒:异常自行处理此处省略
        }
    }

    /**
     * 监听Nacos Server下发的灰度发布配置
     * @PostConstruct   加上该注解表明该方法会在bean初始化后调用
     */
    @PostConstruct
    public void grayscaleInfoByNacosListener() {
        try {
            String[] str = discovery_conf.split(":");
            String dataId = str[0];
            String group = str[1];
            String content = configService.getConfig(dataId, group, 5000);
            setDiscoveryInfo(content);
            //监听配置中心配置数据变动
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    setDiscoveryInfo(configInfo);
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
            //todo 提醒:异常自行处理此处省略
        }
    }


    private void setClientId(String configInfo){
        log.info(configInfo);
        String[] infos= configInfo.split("\\n");
        for(String info :infos){
            info = info.replaceAll("\\r","");
            String[] str = info.split(":");
            ClientKeyConfig.clientKey.put(str[0],str[1]);
        }
        log.info("ClientKeyConfig.clientKey.size =  "+ClientKeyConfig.clientKey.size());
    }

    private void setGatewayConf(String configInfo){
        log.info(configInfo);
        List<RouteDefinition> list = JSONObject.parseArray(configInfo, RouteDefinition.class);
        list.forEach(definition -> {
            dynamicRouteService.update(definition);
        });
    }

    private void setTokenUrl(String configInfo){
        log.info(configInfo);
        String[] infos= configInfo.split("\\n");
        ClientKeyConfig.nokenUrl.clear();
        for(String info :infos){
            ClientKeyConfig.nokenUrl.put(info,info);
        }
        log.info("ClientKeyConfig.nokenUrl.size =  "+ClientKeyConfig.nokenUrl.size());
    }

    private void setDiscoveryInfo(String configInfo){
        log.info(configInfo);
        try{
            MyDiscoveryEnabledStrategy.serviceMap.clear();
            MyDiscoveryEnabledStrategy.clientVersionMap.clear();
            MyDiscoveryEnabledStrategy.remoteIpMap.clear();
            MyDiscoveryEnabledStrategy.isOpen = false;
            GrayscaleInfo grayscaleInfo = JSONObject.parseObject(configInfo,GrayscaleInfo.class);
            if(grayscaleInfo.isOpen()){//灰度开启
                if(StringUtils.isNotEmpty(grayscaleInfo.getService())){
                    String[] services= grayscaleInfo.getService().split(",");
                    for(String infos :services){
                        String[] info= infos.split(":");
                        MyDiscoveryEnabledStrategy.serviceMap.put("default_group@@"+info[0],info[1]);
                        MyDiscoveryEnabledStrategy.serviceMap.put(info[0],info[1]);
                    }
                }

                if(StringUtils.isNotEmpty(grayscaleInfo.getClientVersion())){
                    String[] infos= grayscaleInfo.getClientVersion().split(",");
                    for(String info :infos){
                        MyDiscoveryEnabledStrategy.clientVersionMap.put(info,info);
                    }
                }

                if(StringUtils.isNotEmpty(grayscaleInfo.getRemoteIp())){
                    String[] infos= grayscaleInfo.getRemoteIp().split(",");
                    for(String info :infos){
                        MyDiscoveryEnabledStrategy.remoteIpMap.put(info,info);
                    }
                }
                log.info("clientVersionMap size = "+MyDiscoveryEnabledStrategy.clientVersionMap.size()+",remoteIpMap size = "+MyDiscoveryEnabledStrategy.remoteIpMap.size()+",serviceMap size = "+MyDiscoveryEnabledStrategy.serviceMap.size());
            }
            if(MyDiscoveryEnabledStrategy.serviceMap.size() >0) {
                if(MyDiscoveryEnabledStrategy.clientVersionMap.size() >0 || MyDiscoveryEnabledStrategy.remoteIpMap.size() >0) {//版本号+远程ip灰度
                    MyDiscoveryEnabledStrategy.isOpen = true;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}