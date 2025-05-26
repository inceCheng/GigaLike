package com.ince.gigalike.model.dto;

import lombok.Data;

/**
 * 话题查询请求DTO
 */
@Data
public class TopicQueryRequest {

    /**
     * 话题名称（模糊搜索）
     */
    private String name;

    /**
     * 话题状态
     */
    private String status;

    /**
     * 是否为官方话题
     */
    private Boolean isOfficial;

    /**
     * 排序字段：post_count(帖子数量)、follow_count(关注数量)、create_time(创建时间)
     */
    private String sortField;

    /**
     * 排序方向：asc(升序)、desc(降序)
     */
    private String sortOrder;

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 页面大小
     */
    private Long pageSize = 10L;
} 