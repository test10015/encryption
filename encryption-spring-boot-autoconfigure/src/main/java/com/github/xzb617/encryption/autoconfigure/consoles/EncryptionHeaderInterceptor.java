package com.github.xzb617.encryption.autoconfigure.consoles;

import cn.hutool.extra.spring.SpringUtil;
import com.github.xzb617.encryption.autoconfigure.utils.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 框架的上下文拦截器
 *
 * @author xzb617
 * @date 2022/5/10 12:36
 * @description:
 */
public class EncryptionHeaderInterceptor implements HandlerInterceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger(EncryptionHeaderInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (RequestUtil.isEncryptionApi()) {
            String headerName = SpringUtil.getProperty("encryption.enable-encryption-header", String.class, "Encryption");
            response.setHeader(headerName, "true");
        }

        return true;
    }

}
