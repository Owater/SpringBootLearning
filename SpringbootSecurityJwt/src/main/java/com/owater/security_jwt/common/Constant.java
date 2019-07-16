package com.owater.security_jwt.common;

/**
 * @Author Owater
 * @Date 2019/7/14
 **/
public class Constant {

    /**
     * 2个小时(以毫秒ms计)
     */
    public static final long EXPIRATION_TIME = 7200;
    /**
     * JWT密码
     */
    public static final String JWT_SECRET = "OwaterSecret";
    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer";
    /**
     * 存放Token的Header Key
     */
    public static final String HEADER_STRING = "Authorization";
}
