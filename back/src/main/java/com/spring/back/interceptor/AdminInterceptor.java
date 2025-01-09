package com.spring.back.interceptor;

import com.spring.back.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果是OPTIONS请求，直接放行
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        
        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        
        token = token.substring(7);
        try {
            String role = jwtUtil.getRole(token);
            if (!"admin".equals(role)) {
                response.setStatus(403);
                return false;
            }
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
} 