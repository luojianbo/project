package com.runfast.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
/**
 * description: 读取client/client_key文件中的clientId和key的值放入静态变量map中
 *
 * @author luojianbo
 * @date 2019/07/16
 */
//@Configuration
//@Slf4j
public class ClientKeyConfig {
    public static final String CLIENTKEY_FILE_URL = "classpath:client/client_key";
    public static Map<String,String> clientKey = new HashMap<>();
    public static Map<String,String> nokenUrl = new HashMap<>();
    //通过noacos配置功能进行配置
//    @Bean
//    public Map<String,String> loadClientKey(){
//        try {
//            Resource resource = new DefaultResourceLoader().getResource(CLIENTKEY_FILE_URL);
//            InputStream in = resource.getInputStream();
//            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
//            while(reader.ready()) {
//                String line = reader.readLine();
//                String[] str = line.split(":");
//                clientKey.put(str[0],str[1]);
//            }
//            log.info("{} file init succ and client size = {}",CLIENTKEY_FILE_URL,clientKey.size());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return clientKey;
//    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
