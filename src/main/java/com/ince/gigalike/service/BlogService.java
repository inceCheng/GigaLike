package com.ince.gigalike.service;

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

    BlogVO getBlogVOById(long blogId, HttpServletRequest request);

    List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request);


}
