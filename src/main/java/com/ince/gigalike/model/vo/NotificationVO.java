package com.ince.gigalike.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 通知视图对象
 */
@Data
public class NotificationVO {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 通知类型
     */
    private String type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 关联资源ID
     */
    private Long relatedId;
    
    /**
     * 关联资源类型
     */
    private String relatedType;
    
    /**
     * 是否已读
     */
    private Integer isRead;
    
    /**
     * 阅读时间
     */
    private Date readTime;
    
    /**
     * 额外数据
     */
    private Map<String, Object> extraData;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 发送者信息
     */
    private SenderInfo sender;
    
    /**
     * 发送者信息内部类
     */
    @Data
    public static class SenderInfo {
        /**
         * 发送者ID
         */
        private Long id;
        
        /**
         * 发送者用户名
         */
        private String username;
        
        /**
         * 发送者显示名称
         */
        private String displayName;
        
        /**
         * 发送者头像
         */
        private String avatarUrl;
    }
} 