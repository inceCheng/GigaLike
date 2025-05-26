package com.ince.gigalike.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 话题创建请求DTO
 */
@Data
public class TopicCreateRequest {

    /**
     * 话题名称
     */
    @NotBlank(message = "话题名称不能为空")
    @Size(min = 1, max = 100, message = "话题名称长度必须在1-100个字符之间")
    private String name;

    /**
     * 话题描述
     */
    @Size(max = 500, message = "话题描述不能超过500个字符")
    private String description;

    /**
     * 话题封面图片URL
     */
    private String coverImage;

    /**
     * 话题主题色，十六进制颜色值
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色值格式不正确，请使用十六进制格式如#1890ff")
    private String color;
} 