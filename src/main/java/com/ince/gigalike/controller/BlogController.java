package com.ince.gigalike.controller;

import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.vo.BlogVO;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("blog")
public class BlogController {
    @Resource
    private BlogService blogService;

    @GetMapping("/get")
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
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.list();
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }

}
