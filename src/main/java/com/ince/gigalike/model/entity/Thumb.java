package com.ince.gigalike.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @TableName thumb
 */
@Data
@TableName(value = "thumb")
public class Thumb {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private Long blogId;

    /**
     * 创建时间
     */
    private Date createTime;

}