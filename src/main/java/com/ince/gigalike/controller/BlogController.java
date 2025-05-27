package com.ince.gigalike.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.dto.BlogCreateRequest;
import com.ince.gigalike.model.dto.BlogSearchRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.vo.BlogVO;
import com.ince.gigalike.model.vo.TopicVO;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.service.TopicService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("blog")
@Tag(name = "博客管理", description = "博客相关接口")
public class BlogController {
    @Resource
    private BlogService blogService;
    
    @Resource
    private TopicService topicService;

    /**
     * 创建博客（包含话题标签）
     */
    @PostMapping("/create")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "创建博客", description = "用户创建新的博客文章，可以添加话题标签")
    public BaseResponse<Long> createBlog(@RequestBody @Valid BlogCreateRequest blogCreateRequest,
                                        HttpServletRequest request) {
        Long blogId = blogService.createBlog(blogCreateRequest, request);
        return ResultUtils.success(blogId);
    }

    /**
     * 获取博客详情
     */
    @GetMapping("/get")
    @Operation(summary = "获取博客详情", description = "根据博客ID获取博客详细信息，包含话题标签")
    public BaseResponse<BlogVO> get(long blogId, HttpServletRequest request) {
        BlogVO blogVO = blogService.getBlogVOById(blogId, request);
        return ResultUtils.success(blogVO);
    }

    /**
     * 博客列表
     * 支持根据话题进行筛选，话题参数为可选项
     *
     * @param topicId 话题ID（可选）
     * @param request
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "获取博客列表", description = "获取博客列表，支持根据话题筛选，包含话题标签信息")
    public BaseResponse<List<BlogVO>> list(@RequestParam(required = false) Long topicId,
                                          HttpServletRequest request) {
        List<Blog> blogList;
        if (topicId != null) {
            // 根据话题查询博客列表
            blogList = blogService.getBlogsByTopicId(topicId);
        } else {
            // 查询所有博客
            blogList = blogService.list();
        }
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }

    /**
     * 更新博客话题标签
     */
    @PostMapping("/topics/update")
    @AuthCheck(mustLogin = true)
    @Operation(summary = "更新博客话题", description = "更新指定博客的话题标签，支持话题名称，不存在的话题会自动创建")
    public BaseResponse<Boolean> updateBlogTopics(@RequestParam Long blogId,
                                                 @RequestBody List<String> topicNames,
                                                 HttpServletRequest request) {
        Boolean result = blogService.updateBlogTopics(blogId, topicNames, request);
        return ResultUtils.success(result);
    }

    /**
     * 获取热门话题
     */
    @GetMapping("/hot-topics")
    @Operation(summary = "获取热门话题", description = "获取当前10大热门话题，包含话题名称和ID")
    public BaseResponse<List<TopicVO>> getHotTopics(HttpServletRequest request) {
        // 获取前10个热门话题
        List<TopicVO> hotTopics = topicService.getHotTopics(10, request);
        return ResultUtils.success(hotTopics);
    }

    /**
     * 搜索博客
     */
    @PostMapping("/search")
    @Operation(summary = "搜索博客", description = "根据关键词搜索博客，支持搜索标题、内容、话题，支持分页和排序")
    public BaseResponse<Page<BlogVO>> searchBlogs(@RequestBody @Valid BlogSearchRequest searchRequest,
                                                 HttpServletRequest request) {
        Page<BlogVO> blogPage = blogService.searchBlogs(searchRequest, request);
        return ResultUtils.success(blogPage);
    }

}
