package com.ince.gigalike.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.mapper.TopicMapper;
import com.ince.gigalike.mapper.UserTopicFollowMapper;
import com.ince.gigalike.model.dto.TopicCreateRequest;
import com.ince.gigalike.model.dto.TopicQueryRequest;
import com.ince.gigalike.model.entity.Topic;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.model.entity.UserTopicFollow;
import com.ince.gigalike.model.vo.TopicVO;
import com.ince.gigalike.model.vo.UserVO;
import com.ince.gigalike.service.TopicService;
import com.ince.gigalike.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 话题服务实现类
 */
@Service
@Slf4j
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    private UserService userService;

    @Resource
    private UserTopicFollowMapper userTopicFollowMapper;

    @Override
    public Long createTopic(TopicCreateRequest topicCreateRequest, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 检查话题名称是否已存在
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", topicCreateRequest.getName());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "话题名称已存在");
        }

        // 创建话题
        Topic topic = new Topic();
        BeanUtils.copyProperties(topicCreateRequest, topic);
        topic.setCreatorId(loginUser.getId());
        topic.setStatus("active");
        topic.setPostCount(0);
        topic.setFollowCount(0);
        topic.setIsOfficial(false);
        topic.setCreateTime(new Date());
        topic.setUpdateTime(new Date());

        // 设置默认颜色
        if (StringUtils.isBlank(topic.getColor())) {
            topic.setColor("#1890ff");
        }

        boolean saveResult = this.save(topic);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建话题失败");
        }

        return topic.getId();
    }

    @Override
    public TopicVO getTopicVO(Long topicId, HttpServletRequest request) {
        Topic topic = this.getById(topicId);
        if (topic == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "话题不存在");
        }
        return getTopicVO(topic, request);
    }

    @Override
    public IPage<TopicVO> listTopicVOByPage(TopicQueryRequest topicQueryRequest, HttpServletRequest request) {
        long current = topicQueryRequest.getCurrent();
        long size = topicQueryRequest.getPageSize();

        // 构建查询条件
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        
        // 话题名称模糊搜索
        if (StringUtils.isNotBlank(topicQueryRequest.getName())) {
            queryWrapper.like("name", topicQueryRequest.getName())
                    .or()
                    .like("description", topicQueryRequest.getName());
        }
        
        // 话题状态
        if (StringUtils.isNotBlank(topicQueryRequest.getStatus())) {
            queryWrapper.eq("status", topicQueryRequest.getStatus());
        } else {
            queryWrapper.eq("status", "active"); // 默认只查询活跃话题
        }
        
        // 是否官方话题
        if (topicQueryRequest.getIsOfficial() != null) {
            queryWrapper.eq("is_official", topicQueryRequest.getIsOfficial());
        }

        // 排序
        String sortField = topicQueryRequest.getSortField();
        String sortOrder = topicQueryRequest.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            boolean isAsc = "asc".equals(sortOrder);
            switch (sortField) {
                case "post_count":
                    queryWrapper.orderBy(true, isAsc, "post_count");
                    break;
                case "follow_count":
                    queryWrapper.orderBy(true, isAsc, "follow_count");
                    break;
                case "create_time":
                    queryWrapper.orderBy(true, isAsc, "create_time");
                    break;
                default:
                    queryWrapper.orderByDesc("post_count"); // 默认按帖子数量降序
            }
        } else {
            queryWrapper.orderByDesc("post_count");
        }

        Page<Topic> topicPage = this.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        List<TopicVO> topicVOList = getTopicVOList(topicPage.getRecords(), request);
        
        Page<TopicVO> topicVOPage = new Page<>(current, size, topicPage.getTotal());
        topicVOPage.setRecords(topicVOList);
        
        return topicVOPage;
    }

    @Override
    public List<TopicVO> searchTopics(String keyword, HttpServletRequest request) {
        if (StringUtils.isBlank(keyword)) {
            return List.of();
        }
        
        List<Topic> topicList = this.baseMapper.searchTopics(keyword);
        return getTopicVOList(topicList, request);
    }

    @Override
    public List<TopicVO> getHotTopics(Integer limit, HttpServletRequest request) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        List<Topic> topicList = this.baseMapper.selectHotTopics(limit);
        return getTopicVOList(topicList, request);
    }

    @Override
    public Boolean followTopic(Long topicId, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 检查话题是否存在
        Topic topic = this.getById(topicId);
        if (topic == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "话题不存在");
        }

        // 检查是否已关注
        QueryWrapper<UserTopicFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId())
                .eq("topic_id", topicId);
        long count = userTopicFollowMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已关注该话题");
        }

        // 创建关注记录
        UserTopicFollow userTopicFollow = new UserTopicFollow();
        userTopicFollow.setUserId(loginUser.getId());
        userTopicFollow.setTopicId(topicId);
        userTopicFollow.setCreateTime(new Date());

        return userTopicFollowMapper.insert(userTopicFollow) > 0;
    }

    @Override
    public Boolean unfollowTopic(Long topicId, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 删除关注记录
        QueryWrapper<UserTopicFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId())
                .eq("topic_id", topicId);

        return userTopicFollowMapper.delete(queryWrapper) > 0;
    }

    @Override
    public List<TopicVO> getUserFollowedTopics(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        List<Topic> topicList = this.baseMapper.selectFollowedTopicsByUserId(loginUser.getId());
        return getTopicVOList(topicList, request);
    }

    @Override
    public List<TopicVO> getTopicsByBlogId(Long blogId, HttpServletRequest request) {
        List<Topic> topicList = this.baseMapper.selectTopicsByBlogId(blogId);
        return getTopicVOList(topicList, request);
    }

    @Override
    public TopicVO getTopicVO(Topic topic, HttpServletRequest request) {
        if (topic == null) {
            return null;
        }

        TopicVO topicVO = new TopicVO();
        BeanUtils.copyProperties(topic, topicVO);

        // 设置创建者信息
        if (topic.getCreatorId() != null) {
            User creator = userService.getById(topic.getCreatorId());
            if (creator != null) {
                UserVO creatorVO = new UserVO();
                BeanUtils.copyProperties(creator, creatorVO);
                topicVO.setCreator(creatorVO);
            }
        }

        // 设置当前用户是否已关注该话题
        try {
            User loginUser = userService.getLoginUser(request);
            if (loginUser != null) {
                int followCount = userTopicFollowMapper.checkUserFollowTopic(loginUser.getId(), topic.getId());
                topicVO.setIsFollowed(followCount > 0);
            } else {
                topicVO.setIsFollowed(false);
            }
        } catch (Exception e) {
            topicVO.setIsFollowed(false);
        }

        return topicVO;
    }

    @Override
    public List<TopicVO> getTopicVOList(List<Topic> topicList, HttpServletRequest request) {
        if (topicList == null || topicList.isEmpty()) {
            return List.of();
        }

        // 获取当前登录用户
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            // 用户未登录，忽略
        }

        // 批量获取创建者信息
        Set<Long> creatorIds = topicList.stream()
                .map(Topic::getCreatorId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        
        Map<Long, User> creatorMap = Map.of();
        if (!creatorIds.isEmpty()) {
            List<User> creators = userService.listByIds(creatorIds);
            creatorMap = creators.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        }

        // 批量获取用户关注状态
        Set<Long> followedTopicIds = Set.of();
        if (loginUser != null) {
            List<Long> topicIds = topicList.stream()
                    .map(Topic::getId)
                    .collect(Collectors.toList());
            followedTopicIds = userTopicFollowMapper.getFollowedTopicIds(loginUser.getId(), topicIds)
                    .stream()
                    .collect(Collectors.toSet());
        }

        // 转换为VO
        final Set<Long> finalFollowedTopicIds = followedTopicIds;
        final Map<Long, User> finalCreatorMap = creatorMap;
        
        return topicList.stream().map(topic -> {
            TopicVO topicVO = new TopicVO();
            BeanUtils.copyProperties(topic, topicVO);

            // 设置创建者信息
            if (topic.getCreatorId() != null) {
                User creator = finalCreatorMap.get(topic.getCreatorId());
                if (creator != null) {
                    UserVO creatorVO = new UserVO();
                    BeanUtils.copyProperties(creator, creatorVO);
                    topicVO.setCreator(creatorVO);
                }
            }

            // 设置关注状态
            topicVO.setIsFollowed(finalFollowedTopicIds.contains(topic.getId()));

            return topicVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Topic getOrCreateTopicByName(String topicName, HttpServletRequest request) {
        if (StringUtils.isBlank(topicName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "话题名称不能为空");
        }

        // 清理话题名称（去除前后空格，限制长度）
        topicName = topicName.trim();
        if (topicName.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "话题名称不能超过100个字符");
        }

        // 先尝试查找现有话题
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", topicName);
        Topic existingTopic = this.getOne(queryWrapper);
        
        if (existingTopic != null) {
            return existingTopic;
        }

        // 话题不存在，创建新话题
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            // 如果用户未登录，使用系统默认创建者（管理员）
            loginUser = userService.getById(1L);
        }

        Topic newTopic = new Topic();
        newTopic.setName(topicName);
        newTopic.setDescription("用户创建的话题：" + topicName);
        newTopic.setColor("#1890ff"); // 默认颜色
        newTopic.setStatus("active");
        newTopic.setPostCount(0);
        newTopic.setFollowCount(0);
        newTopic.setIsOfficial(false); // 用户创建的话题默认不是官方话题
        newTopic.setCreatorId(loginUser != null ? loginUser.getId() : 1L);
        newTopic.setCreateTime(new Date());
        newTopic.setUpdateTime(new Date());

        boolean saveResult = this.save(newTopic);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建话题失败");
        }

        String creatorName = loginUser != null ? loginUser.getUsername() : "system";
        log.info("自动创建新话题: {}, 创建者: {}", topicName, creatorName);

        return newTopic;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Topic> getOrCreateTopicsByNames(List<String> topicNames, HttpServletRequest request) {
        if (topicNames == null || topicNames.isEmpty()) {
            return List.of();
        }

        // 去重并过滤空值
        List<String> uniqueTopicNames = topicNames.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        if (uniqueTopicNames.size() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多只能选择10个话题");
        }

        List<Topic> topics = new ArrayList<>();
        for (String topicName : uniqueTopicNames) {
            Topic topic = getOrCreateTopicByName(topicName, request);
            topics.add(topic);
        }

        return topics;
    }
} 