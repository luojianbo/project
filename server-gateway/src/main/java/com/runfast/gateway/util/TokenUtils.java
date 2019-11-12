package com.runfast.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.lang.StringUtils;

import static com.auth0.jwt.JWT.decode;

/**
 * Description:token工具栏
 * Created by luojianbo on 2019/7/27 10:47
 */
public class TokenUtils {
    public final static String TOKEN="token";
    public final static String SCRETE="wdptkc88";
    public final static String ISSUER = "runfast";
    public final static String USER_ID = "userId";

    public static Integer getUserId(String token) {
        JWT decode = decode(token);
        Claim claim = decode.getClaim(TokenUtils.USER_ID);
        Integer userId = claim.asInt();
        return userId;
    }
    /**
     * 检查token有效性
     * @param token
     * @return
     * Created by luojianbo on 2019/7/27 10:47
     */
    public static boolean check(String token){
        if(StringUtils.isEmpty(token)){
            return false;
        }
        try {
            TokenUtils.getUserId(token);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
