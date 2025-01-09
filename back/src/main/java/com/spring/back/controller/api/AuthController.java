package com.spring.back.controller.api;

import com.spring.back.model.User;
import com.spring.back.model.Result;
import com.spring.back.service.UserService;
import com.spring.back.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody User user) {
        Result<User> result = userService.register(user);
        if (result.isSuccess()) {
            String token = jwtUtil.generateToken(result.getData().getId(), result.getData().getRole());
            return Result.success(token);
        }
        return Result.error(result.getMessage());
    }
    
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody User user) {
        Result<User> result = userService.login(user.getUsername(), user.getPassword());
        if (result.isSuccess()) {
            String token = jwtUtil.generateToken(result.getData().getId(), result.getData().getRole());
            return Result.success(token);
        }
        return Result.error(result.getMessage());
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            return Result.error("请先登录");
        }
        Integer userId = Integer.parseInt(userIdStr);
        return userService.findById(userId);
    }
    
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            return Result.error("请先登录");
        }
        Integer userId = Integer.parseInt(userIdStr);
        
        try {
            String avatarUrl = userService.updateAvatar(userId, file);
            return Result.success(avatarUrl);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
} 