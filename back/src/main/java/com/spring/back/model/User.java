package com.spring.back.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String role;
    
    private Boolean status;
    
    private String avatar;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 