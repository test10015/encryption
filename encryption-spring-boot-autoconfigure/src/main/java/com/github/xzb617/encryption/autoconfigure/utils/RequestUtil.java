package com.github.xzb617.encryption.autoconfigure.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @date 2024/03/22 15:56
 **/
public class RequestUtil {

    public static boolean isEncryptionApi(String headerName) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return false;
        }

        HttpServletRequest request = requestAttributes.getRequest();
        String header = request.getHeader(headerName);
        return "true".equals(header);
    }


    public static boolean isEncryptionApi() {
        String headerName = SpringUtil.getProperty("encryption.enable-encryption-header", String.class, "Encryption");

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return false;
        }

        HttpServletRequest request = requestAttributes.getRequest();
        String header = request.getHeader(headerName);
        return "true".equals(header);
    }

}
