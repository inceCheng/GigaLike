package com.ince.gigalike.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ince.gigalike.model.dto.BlogCreateRequest;
import com.ince.gigalike.model.dto.BlogSearchRequest;
import com.ince.gigalike.model.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ince.gigalike.model.vo.BlogVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author inceCheng
* @description 针对表【blog】的数据库操作Service
* @createDate 2025-05-14 13:52:19
*/
public interface BlogService extends IService<Blog> {

    /**
     * 创建博客（包含话题标签）
     */
    Long createBlog(BlogCreateRequest blogCreateRequest, HttpServletRequest request);

    /**
     * 根据ID获取博客VO（包含话题信息）
     */
    BlogVO getBlogVOById(long blogId, HttpServletRequest request);

    /**
     * 批量获取博客VO列表（包含话题信息）
     */
    List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request);

    /**
     * 更新博客话题标签
     */
    Boolean updateBlogTopics(Long blogId, List<String> topicNames, HttpServletRequest request);

    /**
     * 根据话题ID查询博客列表
     */
    List<Blog> getBlogsByTopicId(Long topicId);

    /**
     * 搜索博客（支持标题、内容、话题模糊查找）
     */
    Page<BlogVO> searchBlogs(BlogSearchRequest searchRequest, HttpServletRequest request);

}
