package com.spring.back.controller.admin;

import com.spring.back.model.Result;
import com.spring.back.annotation.RequireAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
@RequireAdmin
public class AdminFileController {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @PostMapping
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新的文件名
            String fileName = UUID.randomUUID().toString() + suffix;
            
            // 获取上传目录的绝对路径
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            
            // 确保上传目录存在
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 创建完整的文件路径
            Path filePath = uploadDir.resolve(fileName);
            
            // 保存文件
            Files.copy(file.getInputStream(), filePath);
            
            String url = "/uploads/" + fileName;
            log.info("文件上传成功：{}", url);
            
            // 返回文件访问URL
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
} 