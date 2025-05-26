package com.ince.gigalike.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ince.gigalike.common.PageRequest;
import com.ince.gigalike.constant.ThumbConstant;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.mapper.BlogTopicMapper;
import com.ince.gigalike.model.dto.BlogCreateRequest;
import com.ince.gigalike.model.entity.Blog;
import com.ince.gigalike.model.entity.BlogTopic;
import com.ince.gigalike.model.entity.Thumb;
import com.ince.gigalike.model.entity.Topic;
import com.ince.gigalike.model.entity.User;
import com.ince.gigalike.model.vo.BlogVO;
import com.ince.gigalike.model.vo.TopicVO;
import com.ince.gigalike.model.vo.UserVO;
import com.ince.gigalike.service.BlogService;
import com.ince.gigalike.mapper.BlogMapper;
import com.ince.gigalike.service.ThumbService;
import com.ince.gigalike.service.TopicService;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.service.FileUploadService;
import com.ince.gigalike.utils.RedisKeyUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author inceCheng
 * @description 针对表【blog】的数据库操作Service实现
 * @createDate 2025-05-14 13:52:19
 */
@Service
@Slf4j
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private TopicService topicService;

    @Resource
    private BlogTopicMapper blogTopicMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private FileUploadService fileUploadService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBlog(BlogCreateRequest blogCreateRequest, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 参数校验
        if (StringUtils.isBlank(blogCreateRequest.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题不能为空");
        }
        if (StringUtils.isBlank(blogCreateRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容不能为空");
        }

        // 创建博客
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogCreateRequest, blog);
        blog.setUserid(loginUser.getId());
        blog.setThumbCount(0);
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());

        boolean saveResult = this.save(blog);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建博客失败");
        }

        // 如果有封面图片且是临时路径，需要更新为正式路径
        if (StringUtils.isNotBlank(blog.getCoverImg()) && blog.getCoverImg().contains("/temp/")) {
            String newImageUrl = updateImagePathWithBlogId(blog.getCoverImg(), blog.getId(), loginUser.getId(), blog.getTitle());
            if (StringUtils.isNotBlank(newImageUrl)) {
                blog.setCoverImg(newImageUrl);
                this.updateById(blog);
            }
        }

        // 处理话题标签
        List<String> topicNames = blogCreateRequest.getTopicNames();
        if (topicNames != null && !topicNames.isEmpty()) {
            // 限制最多10个话题
            if (topicNames.size() > 10) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多只能选择10个话题");
            }

            // 根据话题名称获取或创建话题
            List<Topic> topics = topicService.getOrCreateTopicsByNames(topicNames, request);

            // 批量插入博客话题关联
            List<BlogTopic> blogTopics = topics.stream()
                    .map(topic -> {
                        BlogTopic blogTopic = new BlogTopic();
                        blogTopic.setBlogId(blog.getId());
                        blogTopic.setTopicId(topic.getId());
                        blogTopic.setCreateTime(new Date());
                        return blogTopic;
                    })
                    .collect(Collectors.toList());

            // 批量插入
            if (!blogTopics.isEmpty()) {
                blogTopicMapper.batchInsert(blogTopics);
            }
        }

        return blog.getId();
    }

    @Override
    public BlogVO getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        if (blog == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "博客不存在");
        }

        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            // 用户未登录，忽略
        }

        return this.getBlogVO(blog, loginUser, request);
    }

    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        if (blogList == null || blogList.isEmpty()) {
            return List.of();
        }

        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            // 用户未登录，忽略
        }

        // 获取点赞状态
        Map<Long, Boolean> blogIdHasThumbMap = new HashMap<>();
        if (loginUser != null) {
            List<Object> blogIdList = blogList.stream()
                    .map(blog -> blog.getId().toString())
                    .collect(Collectors.toList());
            
            List<Object> thumbList = redisTemplate.opsForHash()
                    .multiGet(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogIdList);
            
            for (int i = 0; i < thumbList.size(); i++) {
                if (thumbList.get(i) != null) {
                    blogIdHasThumbMap.put(Long.valueOf(blogIdList.get(i).toString()), true);
                }
            }
        }

        // 批量获取作者信息
        Set<Long> userIds = blogList.stream()
                .map(Blog::getUserid)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 批量获取话题信息
        Map<Long, List<TopicVO>> blogTopicsMap = new HashMap<>();
        for (Blog blog : blogList) {
            List<TopicVO> topics = topicService.getTopicsByBlogId(blog.getId(), request);
            blogTopicsMap.put(blog.getId(), topics);
        }

        // 转换为VO
        return blogList.stream()
                .map(blog -> {
                    BlogVO blogVO = new BlogVO();
                    BeanUtils.copyProperties(blog, blogVO);
                    
                    // 设置点赞状态
                    blogVO.setHasThumb(blogIdHasThumbMap.getOrDefault(blog.getId(), false));
                    
                    // 设置作者信息
                    User author = userMap.get(blog.getUserid());
                    if (author != null) {
                        UserVO authorVO = new UserVO();
                        BeanUtils.copyProperties(author, authorVO);
                        blogVO.setAuthor(authorVO);
                    }
                    
                    // 设置话题信息
                    blogVO.setTopics(blogTopicsMap.get(blog.getId()));
                    
                    return blogVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBlogTopics(Long blogId, List<String> topicNames, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 检查博客是否存在且属于当前用户
        Blog blog = this.getById(blogId);
        if (blog == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "博客不存在");
        }
        if (!blog.getUserid().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改此博客");
        }

        // 删除原有的话题关联
        blogTopicMapper.deleteByBlogId(blogId);

        // 添加新的话题关联
        if (topicNames != null && !topicNames.isEmpty()) {
            // 限制最多10个话题
            if (topicNames.size() > 10) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多只能选择10个话题");
            }

            // 根据话题名称获取或创建话题
            List<Topic> topics = topicService.getOrCreateTopicsByNames(topicNames, request);

            List<BlogTopic> blogTopics = topics.stream()
                    .map(topic -> {
                        BlogTopic blogTopic = new BlogTopic();
                        blogTopic.setBlogId(blogId);
                        blogTopic.setTopicId(topic.getId());
                        blogTopic.setCreateTime(new Date());
                        return blogTopic;
                    })
                    .collect(Collectors.toList());

            // 批量插入
            if (!blogTopics.isEmpty()) {
                blogTopicMapper.batchInsert(blogTopics);
            }
        }

        return true;
    }

    private BlogVO getBlogVO(Blog blog, User loginUser, HttpServletRequest request) {
        BlogVO blogVO = new BlogVO();
        BeanUtils.copyProperties(blog, blogVO);

        // 设置作者信息
        User author = userService.getById(blog.getUserid());
        if (author != null) {
            UserVO authorVO = new UserVO();
            BeanUtils.copyProperties(author, authorVO);
            blogVO.setAuthor(authorVO);
        }

        // 设置话题信息
        List<TopicVO> topics = topicService.getTopicsByBlogId(blog.getId(), request);
        blogVO.setTopics(topics);

        // 设置点赞状态
        if (loginUser != null) {
            Boolean hasThumb = thumbService.hasThumb(blog.getId(), loginUser.getId());
            blogVO.setHasThumb(hasThumb);
        } else {
            blogVO.setHasThumb(false);
        }

        return blogVO;
    }

    /**
     * 更新图片路径中的blogId（从临时路径更新为正式路径）
     */
    private String updateImagePathWithBlogId(String tempImageUrl, Long blogId, Long userId, String title) {
        try {
            // 从临时URL中提取文件名
            String fileName = extractFileNameFromUrl(tempImageUrl);
            if (StringUtils.isBlank(fileName)) {
                return null;
            }

            // 生成新的文件路径
            String newFilePath = fileUploadService.generateFilePath(userId, blogId, title, fileName);
            
            // 使用COS复制文件到新路径
            String newImageUrl = fileUploadService.copyFile(tempImageUrl, newFilePath);
            
            return newImageUrl;
        } catch (Exception e) {
            log.error("更新图片路径失败，tempImageUrl: {}, blogId: {}", tempImageUrl, blogId);
            log.error("异常详情", e);
            return null;
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return null;
        }
        
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        if (lastSlashIndex > 0 && lastSlashIndex < imageUrl.length() - 1) {
            return imageUrl.substring(lastSlashIndex + 1);
        }
        
        return null;
    }
}




