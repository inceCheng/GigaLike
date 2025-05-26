package com.ince.gigalike.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 博客创建请求DTO
 */
@Data
public class BlogCreateRequest {

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 512, message = "文章标题不能超过512个字符")
    private String title;

    /**
     * 文章封面图片URL
     */
    private String coverImg;

    /**
     * 文章正文内容
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 话题名称列表，最多10个
     * 如果话题不存在，系统会自动创建
     */
    @Size(max = 10, message = "最多只能选择10个话题")
    private List<String> topicNames;
} 