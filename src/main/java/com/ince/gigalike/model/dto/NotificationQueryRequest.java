package com.ince.gigalike.model.dto;

import lombok.Data;

/**
 * 通知查询请求
 */
@Data
public class NotificationQueryRequest {
    
    /**
     * 当前页码
     */
    private int current = 1;
    
    /**
     * 页面大小
     */
    private int pageSize = 10;
    
    /**
     * 是否已读：null-全部，0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 通知类型：LIKE, COMMENT, FOLLOW, SYSTEM
     */
    private String type;
} 