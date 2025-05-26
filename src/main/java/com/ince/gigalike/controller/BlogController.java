package com.ince.gigalike.controller;

import com.ince.gigalike.annotation.AuthCheck;
import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.dto.BlogCreateRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.vo.BlogVO;
import com.ince.gigalike.service.BlogService;
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
     * 这部分不是我们项目的核心，所以这里不做各种条件的查询，仅把 blog 表中的数据查出返回给前端展示即可。
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "获取博客列表", description = "获取所有博客列表，包含话题标签信息")
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.list();
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

}
