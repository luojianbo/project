package com.runfast.nacos;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.runfast.discovery.MyDiscoveryEnabledStrategy;
import com.runfast.nacos.vo.GrayscaleInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;

/**
 * 监听Nacos Server下发的配置处理器
 * @author luojianbo
 * @date 2019/9/29 15:38
 */
@Component
@Slf4j
public class NacosConfigHandler {

    private String discovery_conf = "discovery_conf:discovery_group";

    @Autowired
    private ConfigService configService;


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