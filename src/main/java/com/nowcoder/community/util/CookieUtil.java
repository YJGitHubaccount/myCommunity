package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){
        if (request == null || StringUtils.isBlank(name)){
            throw new IllegalArgumentException("参数为空!");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
