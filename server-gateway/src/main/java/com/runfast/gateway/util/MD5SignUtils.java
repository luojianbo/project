package com.runfast.gateway.util;


import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.runfast.gateway.vo.RequestHeadVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 类MD5Sign.java的实现描述：MD5签名和验签
 *
 * @author leon 2016年10月10日 下午2:52:04
 */
public class MD5SignUtils {
    private static  Logger logger = LoggerFactory.getLogger(MD5SignUtils.class);
    /**
     * 方法描述:将字符串MD5加码 生成32位md5码
     *
     * @author leon 2016年10月10日 下午3:02:30
     * @param inStr
     * @return
     */
    public static String md5(String inStr) {
        if(StringUtils.isNotEmpty(inStr)){
            try {
                return DigestUtils.md5Hex(inStr.getBytes("UTF-8")).toUpperCase();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("MD5签名过程中出现错误");
            }

        }
        return "";
    }

    /**
     * 方法描述:签名字符串
     *
     * @author leon 2016年10月10日 下午2:54:47
     * @param params 需要签名的参数
     * @param appSecret 签名密钥
     * @return
     */
    public static String sign(Map<String, String> params, RequestHeadVo headparams, String jsonbody,String appSecret) {
        StringBuilder valueSb = new StringBuilder();
        // 将参数以参数名的字典升序排序
        Map<String, Object> sortParams = new TreeMap<String, Object>(params);
        Set<Entry<String, Object>> entrys = sortParams.entrySet();
        // 遍历排序的字典,并拼接value1+value2......格式
        for (Entry<String, Object> entry : entrys) {
            valueSb.append(entry.getKey()+"="+entry.getValue()+"&");
        }

        if(valueSb.toString().endsWith("&")){
            valueSb.deleteCharAt(valueSb.length()-1);
        }
        valueSb.append(jsonbody==null ? "":jsonbody);
        valueSb.append(headparams.getClientId()==null ? "":headparams.getClientId());
        valueSb.append(headparams.getTimestamp()==null ? "":headparams.getTimestamp());
        valueSb.append(headparams.getClientVersion()==null ? "":headparams.getClientVersion());
        valueSb.append(headparams.getToken()==null ? "":headparams.getToken());
        valueSb.append(appSecret==null ? "":appSecret);
        logger.debug("签名字符串：{}",valueSb.toString());
        // 此处删除appSecret，因为appSecret不能出现在url上,否则生成MD5签名无法验证

        return md5(valueSb.toString());
    }




    public static void main(String[] str){
        System.out.println(md5("sn=qwertyuiop43&type=P682000011571297507716V1.0eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhbGlhcyI6Ijg2NjY1NDAzNjkxOTE4OCIsImlkIjoxMjIzMTIsInV1aWQiOiI1YzQ2NjdmM2MzYjc0OGJjODBmNzg4OTk5M2IxOGFmNiIsImlhdCI6MTU2ODE3NTA2OSwicGxhdGZvcm0iOiJhcHAifQ.m0VRgURgQpvB4QwDkv8IS32fftGSZuEDoHrpuI0Nv2Q431fad8dfce68895e4526c89b9eed247"));
    }

}
