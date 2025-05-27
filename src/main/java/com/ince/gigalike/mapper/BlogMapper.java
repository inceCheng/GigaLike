package com.ince.gigalike.mapper;

import com.ince.gigalike.model.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* @author inceCheng
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2025-05-14 13:52:19
* @Entity com.ince.gigalike.model.entity.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {

    void batchUpdateThumbCount(@Param("countMap") Map<Long, Long> countMap);

    /**
     * 根据话题ID查询博客列表
     */
    @Select("SELECT b.* FROM blog b " +
            "JOIN blog_topics bt ON b.id = bt.blog_id " +
            "WHERE bt.topic_id = #{topicId} " +
            "ORDER BY b.createTime DESC")
    List<Blog> selectBlogsByTopicId(@Param("topicId") Long topicId);

}




