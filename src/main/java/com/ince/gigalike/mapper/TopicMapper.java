package com.ince.gigalike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ince.gigalike.model.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 话题Mapper接口
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 根据博客ID查询关联的话题列表
     */
    @Select("SELECT t.* FROM topics t " +
            "JOIN blog_topics bt ON t.id = bt.topic_id " +
            "WHERE bt.blog_id = #{blogId} AND t.status = 'active'")
    List<Topic> selectTopicsByBlogId(@Param("blogId") Long blogId);

    /**
     * 根据用户ID查询关注的话题列表
     */
    @Select("SELECT t.* FROM topics t " +
            "JOIN user_topic_follows utf ON t.id = utf.topic_id " +
            "WHERE utf.user_id = #{userId} AND t.status = 'active' " +
            "ORDER BY utf.create_time DESC")
    List<Topic> selectFollowedTopicsByUserId(@Param("userId") Long userId);

    /**
     * 查询热门话题（按帖子数量排序）
     */
    @Select("SELECT * FROM topics " +
            "WHERE status = 'active' " +
            "ORDER BY post_count DESC " +
            "LIMIT #{limit}")
    List<Topic> selectHotTopics(@Param("limit") Integer limit);

    /**
     * 搜索话题（模糊匹配名称或描述）
     */
    @Select("SELECT * FROM topics " +
            "WHERE (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = 'active' " +
            "ORDER BY post_count DESC")
    List<Topic> searchTopics(@Param("keyword") String keyword);
} 