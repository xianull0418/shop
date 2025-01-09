package com.spring.back.interceptor;

import com.spring.back.exception.BusinessException;
import com.spring.back.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行OPTIONS请求
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }

        // 验证token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Token缺失或格式错误: {}", token);
            throw new BusinessException("请先登录");
        }

        token = token.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            // 将用户ID设置到request属性中
            request.setAttribute("userId", claims.getSubject());
            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            throw new BusinessException("登录已过期，请重新登录");
        }
    }
} 