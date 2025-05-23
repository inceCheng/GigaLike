package com.ince.gigalike.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateRequest {
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String displayName;

    private String avatarUrl;

    @Size(max = 200, message = "个人简介不能超过200个字符")
    private String bio;

    @Email(message = "邮箱格式不正确")
    private String email;
} 