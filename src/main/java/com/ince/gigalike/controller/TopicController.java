package com.ince.gigalike.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.dto.TopicCreateRequest;
import com.ince.gigalike.model.dto.TopicQueryRequest;
import com.ince.gigalike.model.vo.TopicVO;
import com.ince.gigalike.service.TopicService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 话题控制器
 */
@RestController
@RequestMapping("/topic")
@Tag(name = "话题管理", description = "话题相关接口")
public class TopicController {

    @Resource
    private TopicService topicService;

    /**
     * 创建话题
     */
    @PostMapping("/create")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "创建话题", description = "用户可以创建新的话题")
    public BaseResponse<Long> createTopic(@RequestBody @Valid TopicCreateRequest topicCreateRequest,
                                         HttpServletRequest request) {
        Long topicId = topicService.createTopic(topicCreateRequest, request);
        return ResultUtils.success(topicId);
    }

    /**
     * 获取话题详情
     */
    @GetMapping("/get")
    @Operation(summary = "获取话题详情", description = "根据话题ID获取话题详细信息")
    public BaseResponse<TopicVO> getTopicById(@RequestParam Long topicId, HttpServletRequest request) {
        TopicVO topicVO = topicService.getTopicVO(topicId, request);
        return ResultUtils.success(topicVO);
    }

    /**
     * 分页查询话题列表
     */
    @PostMapping("/list")
    @Operation(summary = "分页查询话题", description = "根据条件分页查询话题列表")
    public BaseResponse<IPage<TopicVO>> listTopicsByPage(@RequestBody TopicQueryRequest topicQueryRequest,
                                                        HttpServletRequest request) {
        IPage<TopicVO> topicPage = topicService.listTopicVOByPage(topicQueryRequest, request);
        return ResultUtils.success(topicPage);
    }

    /**
     * 搜索话题
     */
    @GetMapping("/search")
    @Operation(summary = "搜索话题", description = "根据关键词搜索话题")
    public BaseResponse<List<TopicVO>> searchTopics(@RequestParam String keyword, HttpServletRequest request) {
        List<TopicVO> topicList = topicService.searchTopics(keyword, request);
        return ResultUtils.success(topicList);
    }

    /**
     * 获取热门话题
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门话题", description = "获取热门话题列表")
    public BaseResponse<List<TopicVO>> getHotTopics(@RequestParam(defaultValue = "10") Integer limit,
                                                   HttpServletRequest request) {
        List<TopicVO> topicList = topicService.getHotTopics(limit, request);
        return ResultUtils.success(topicList);
    }

    /**
     * 关注话题
     */
    @PostMapping("/follow")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "关注话题", description = "用户关注指定话题")
    public BaseResponse<Boolean> followTopic(@RequestParam Long topicId, HttpServletRequest request) {
        Boolean result = topicService.followTopic(topicId, request);
        return ResultUtils.success(result);
    }

    /**
     * 取消关注话题
     */
    @PostMapping("/unfollow")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "取消关注话题", description = "用户取消关注指定话题")
    public BaseResponse<Boolean> unfollowTopic(@RequestParam Long topicId, HttpServletRequest request) {
        Boolean result = topicService.unfollowTopic(topicId, request);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户关注的话题列表
     */
    @GetMapping("/followed")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "获取关注的话题", description = "获取当前用户关注的话题列表")
    public BaseResponse<List<TopicVO>> getUserFollowedTopics(HttpServletRequest request) {
        List<TopicVO> topicList = topicService.getUserFollowedTopics(request);
        return ResultUtils.success(topicList);
    }

    /**
     * 根据博客ID获取话题列表
     */
    @GetMapping("/blog")
    @Operation(summary = "获取博客的话题", description = "根据博客ID获取关联的话题列表")
    public BaseResponse<List<TopicVO>> getTopicsByBlogId(@RequestParam Long blogId, HttpServletRequest request) {
        List<TopicVO> topicList = topicService.getTopicsByBlogId(blogId, request);
        return ResultUtils.success(topicList);
    }
} 