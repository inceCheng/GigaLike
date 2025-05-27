package com.ince.gigalike.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 博客搜索请求
 */
@Data
@Schema(description = "博客搜索请求")
public class BlogSearchRequest {

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词，支持搜索标题、内容、话题", example = "Java")
    private String keyword;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private long current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private long pageSize = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段：createTime(创建时间)、thumbCount(点赞数)", example = "createTime")
    private String sortField = "createTime";

    /**
     * 排序方式
     */
    @Schema(description = "排序方式：asc(升序)、desc(降序)", example = "desc")
    private String sortOrder = "desc";
} 