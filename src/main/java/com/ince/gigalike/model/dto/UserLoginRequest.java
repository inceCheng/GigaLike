package com.ince.gigalike.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserLoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
    
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
} 