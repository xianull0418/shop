package com.spring.back;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
public class BackApplication {

    @Value("${upload.path}")
    private String uploadPath;

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            // 确保上传目录存在
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 复制默认头像到上传目录
            Path defaultAvatarPath = uploadDir.resolve("default-avatar.png");
            if (!Files.exists(defaultAvatarPath)) {
                // 从resources目录复制默认头像
                File defaultAvatar = ResourceUtils.getFile("classpath:static/images/default-avatar.png");
                Files.copy(defaultAvatar.toPath(), defaultAvatarPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            // 记录错误但不中断应用启动
            e.printStackTrace();
        }
    }
}
