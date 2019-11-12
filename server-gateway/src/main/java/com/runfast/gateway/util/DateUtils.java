package com.runfast.gateway.util;
/**
 *  时间工具栏
 * @author luojianbo
 * @date 2019/6/5
 */
public class DateUtils {
    /**
     *  比较 s1和s相差几分钟,s2-s1,返回相差多少秒
     * @param s1
     * @param s2
     */
    public static long compareToSecond(long s1,long s2){
        return (s2 - s1) / (1000);
    }
    /**
     *  比较 s和当前时间相差几分钟,System.currentTimeMillis-s,返回相差多少秒
     * @param s
     */
    public static long compareToSecond(long s){
        return compareToSecond(s,System.currentTimeMillis());
    }

    /**
     *  比较 s和当前时间相差几分钟,System.currentTimeMillis-s,返回相差多少秒
     * @param s
     */
    public static Long compareToSecond(String s){
        if(s == null){
            return null;
        }
        try{
            return compareToSecond(Long.valueOf(s));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
