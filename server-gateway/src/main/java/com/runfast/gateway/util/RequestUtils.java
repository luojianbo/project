package com.runfast.gateway.util;

import com.runfast.gateway.vo.RequestHeadVo;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  请求工具类
 * @author luojianbo
 * @date 2019/6/5
 */
public class RequestUtils {
    /**
     * 将请求get参数(k1=v1&k2=v2)转换成map
     * @param getStr
     * @return
     */
    public static Map<String,String> getMapData(String getStr){
        HashMap<String, String> dataMap = new HashMap<>();
        try{
            if(StringUtils.isNotEmpty(getStr)){
                String[] strs = getStr.split("&");
                for (int i = 0; i < strs.length; i++) {
                    String[] str = strs[i].split("=",-1);
                    dataMap.put(str[0], str[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return dataMap;
    }

    /**
     * 将map转换成请求get参数(k1=v1&k2=v2)
     * @param unSortedStr
     * @return
     */
    public static String getSortedStr(Map<String, String> unSortedStr) {
        String sortedStr= unSortedStr
                .entrySet()
                .stream()
                .filter(entry -> !StringUtil.isNullOrEmpty(entry.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        return sortedStr;
    }

    /**
     * 返回非空字符串
     * @param str
     * @return
     */
    public static String getNullStr(String str){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        return str;
    }

    /**
     * 获取请求头部参数
     * @param header
     * @return
     */
    public static RequestHeadVo getRequestHeadVo(HttpHeaders header){
        return new RequestHeadVo(header.getFirst("clientId"),header.getFirst("timestamp"),header.getFirst("sign"),header.getFirst("token"),header.getFirst("apiVersion"),header.getFirst("clientVersion"));
    }
}
