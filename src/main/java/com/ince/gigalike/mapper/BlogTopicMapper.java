package com.ince.gigalike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ince.gigalike.model.entity.BlogTopic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 博客话题关联Mapper接口
 */
@Mapper
public interface BlogTopicMapper extends BaseMapper<BlogTopic> {

    /**
     * 删除博客的所有话题关联
     */
    @Delete("DELETE FROM blog_topics WHERE blog_id = #{blogId}")
    int deleteByBlogId(@Param("blogId") Long blogId);

    /**
     * 批量插入博客话题关联
     */
    int batchInsert(@Param("blogTopics") java.util.List<BlogTopic> blogTopics);
} 