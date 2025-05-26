package com.ince.gigalike.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ince.gigalike.model.dto.TopicCreateRequest;
import com.ince.gigalike.model.dto.TopicQueryRequest;
import com.ince.gigalike.model.entity.Topic;
import com.ince.gigalike.model.vo.TopicVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 话题服务接口
 */
public interface TopicService extends IService<Topic> {

    /**
     * 创建话题
     */
    Long createTopic(TopicCreateRequest topicCreateRequest, HttpServletRequest request);

    /**
     * 获取话题详情
     */
    TopicVO getTopicVO(Long topicId, HttpServletRequest request);

    /**
     * 分页查询话题
     */
    IPage<TopicVO> listTopicVOByPage(TopicQueryRequest topicQueryRequest, HttpServletRequest request);

    /**
     * 搜索话题
     */
    List<TopicVO> searchTopics(String keyword, HttpServletRequest request);

    /**
     * 获取热门话题
     */
    List<TopicVO> getHotTopics(Integer limit, HttpServletRequest request);

    /**
     * 关注话题
     */
    Boolean followTopic(Long topicId, HttpServletRequest request);

    /**
     * 取消关注话题
     */
    Boolean unfollowTopic(Long topicId, HttpServletRequest request);

    /**
     * 获取用户关注的话题列表
     */
    List<TopicVO> getUserFollowedTopics(HttpServletRequest request);

    /**
     * 根据博客ID获取话题列表
     */
    List<TopicVO> getTopicsByBlogId(Long blogId, HttpServletRequest request);

    /**
     * 将Topic实体转换为TopicVO
     */
    TopicVO getTopicVO(Topic topic, HttpServletRequest request);

    /**
     * 批量将Topic实体转换为TopicVO
     */
    List<TopicVO> getTopicVOList(List<Topic> topicList, HttpServletRequest request);

    /**
     * 根据话题名称获取或创建话题
     * 如果话题不存在，则自动创建
     */
    Topic getOrCreateTopicByName(String topicName, HttpServletRequest request);

    /**
     * 批量根据话题名称获取或创建话题
     */
    List<Topic> getOrCreateTopicsByNames(List<String> topicNames, HttpServletRequest request);
} 