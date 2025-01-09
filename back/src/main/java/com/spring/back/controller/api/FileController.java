package com.spring.back.controller.api;

import com.spring.back.model.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileController {
    
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
            
            // 创建文件对象
            File dest = new File(uploadPath + fileName);
            // 判断文件父目录是否存在
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            
            // 保存文件
            file.transferTo(dest);
            
            // 返回文件访问URL
            return Result.success("/uploads/" + fileName);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
} 