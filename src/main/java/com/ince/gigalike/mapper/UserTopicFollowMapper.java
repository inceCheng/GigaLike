package com.ince.gigalike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ince.gigalike.model.entity.UserTopicFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户话题关注Mapper接口
 */
@Mapper
public interface UserTopicFollowMapper extends BaseMapper<UserTopicFollow> {

    /**
     * 检查用户是否关注了指定话题
     */
    @Select("SELECT COUNT(*) FROM user_topic_follows " +
            "WHERE user_id = #{userId} AND topic_id = #{topicId}")
    int checkUserFollowTopic(@Param("userId") Long userId, @Param("topicId") Long topicId);

    /**
     * 批量检查用户对话题的关注状态
     */
    @Select("<script>" +
            "SELECT topic_id FROM user_topic_follows " +
            "WHERE user_id = #{userId} AND topic_id IN " +
            "<foreach collection='topicIds' item='topicId' open='(' separator=',' close=')'>" +
            "#{topicId}" +
            "</foreach>" +
            "</script>")
    List<Long> getFollowedTopicIds(@Param("userId") Long userId, @Param("topicIds") List<Long> topicIds);
} 