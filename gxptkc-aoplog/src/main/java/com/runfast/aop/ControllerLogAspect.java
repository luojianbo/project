package com.runfast.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;


/**切面日志输出
 * @author: luojianbo
 * @date: 2018年03月01日
 */
@Component
@Aspect
@Slf4j
public class ControllerLogAspect {

    @Before("execution(* com.runfast..controller..*.*(..)) || execution(* com.gxptkc..controller..*.*(..)) || execution(* com.ptkc..controller..*.*(..))")
    public void doBeforeInServiceLayer(JoinPoint pjp) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        if(!"initBinder".equals(pjp.getSignature().getName())) {
            try {

                log.info("[{}.{}.{}]RequestHeader : {}", pjp.getTarget().getClass().getSimpleName(), pjp.getSignature().getName(), ra.getSessionId(), getHeaderPramater(request));
                String str = getRequestPramater(request, pjp.getArgs());
                log.info("[{}.{}.{}]RequestBody   : {}", pjp.getTarget().getClass().getSimpleName(), pjp.getSignature().getName(), ra.getSessionId(), str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @After("execution(* com.runfast..controller..*.*(..)) || execution(* com.gxptkc..controller..*.*(..)) || execution(* com.ptkc..controller..*.*(..))")
    public void doAfterInServiceLayer(JoinPoint joinPoint) {

    }

    @Around("execution(* com.runfast..controller..*.*(..)) || execution(* com.gxptkc..controller..*.*(..)) || execution(* com.ptkc..controller..*.*(..))")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        Object result = pjp.proceed();
        if(!"initBinder".equals(pjp.getSignature().getName())) {
            try {
                log.info("[{}.{}.{}]ResponseBody  : {}", pjp.getTarget().getClass().getSimpleName(), pjp.getSignature().getName(), ra.getSessionId(), JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    private String getHeaderPramater(HttpServletRequest request){
        StringBuffer sb = new StringBuffer();
        sb.append("method=");
        sb.append(request.getMethod());
        sb.append(";");
        sb.append("remoteIp=");
        sb.append(getIpAddr(request));
        sb.append(";");
        sb.append("clientId=");
        sb.append(request.getHeader("clientId"));
        sb.append(";");
        sb.append("clientVersion=");
        sb.append(request.getHeader("clientVersion"));
        sb.append(";");
        sb.append("token=");
        sb.append(request.getHeader("token"));
        sb.append(";");

//        Enumeration<String> enu = request.getHeaderNames();
//        while (enu.hasMoreElements()) {
//            String paraName = enu.nextElement();
//            sb.append(paraName);
//            sb.append("=");
//            sb.append(request.getHeader(paraName));
//            sb.append(";");
//        }
        return sb.toString();
    }

    private String getRequestPramater(HttpServletRequest request,Object[] args){
        String contentType = request.getContentType();
        //获取请求参数集合并进行遍历拼接
        try {
            //获取参数
            if (contentType != null && contentType.toLowerCase().contains("application/json")) {
                return JSON.toJSONString(args);
            } else {
                //return request.getParameterMap().toString();
                return JSON.toJSONString(request.getParameterMap(), SerializerFeature.WriteMapNullValue);
            }
        }catch(Exception e){
            e.printStackTrace();
            return "RequestPramater Error";
        }

    }

    private  String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
