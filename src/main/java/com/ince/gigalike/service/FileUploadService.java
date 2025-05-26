package com.ince.gigalike.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传博客图片到腾讯云COS
     * 
     * @param file 图片文件
     * @param userId 用户ID
     * @param blogId 博客ID（可为空，创建博客时还没有ID）
     * @param title 博客标题
     * @return 图片访问URL
     */
    String uploadBlogImage(MultipartFile file, Long userId, Long blogId, String title);

    /**
     * 删除COS中的文件
     * 
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    Boolean deleteFile(String fileUrl);

    /**
     * 生成文件路径
     * 
     * @param userId 用户ID
     * @param blogId 博客ID
     * @param title 博客标题
     * @param fileName 文件名
     * @return 文件路径
     */
    String generateFilePath(Long userId, Long blogId, String title, String fileName);

    /**
     * 复制COS中的文件到新路径
     * 
     * @param sourceUrl 源文件URL
     * @param targetPath 目标文件路径
     * @return 新的文件URL
     */
    String copyFile(String sourceUrl, String targetPath);
} 