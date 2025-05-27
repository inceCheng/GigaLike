package com.ince.gigalike.service.Impl;

import com.ince.gigalike.config.CosConfig;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.service.FileUploadService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.CopyObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private COSClient cosClient;

    @Resource
    private CosConfig cosConfig;

    /**
     * 支持的图片格式
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    /**
     * 支持的文件扩展名
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    /**
     * 最大文件大小 10MB
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public String uploadBlogImage(MultipartFile file, Long userId, Long blogId, String title) {
        // 1. 参数验证
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        if (StringUtils.isBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客标题不能为空");
        }

        // 2. 文件类型验证
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的图片格式，仅支持：jpg、png、gif、webp");
        }

        // 3. 文件大小验证
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        }

        // 4. 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件扩展名");
        }

        // 5. 生成文件名和路径
        String fileName = generateFileName() + extension;
        String filePath = generateFilePath(userId, blogId, title, fileName);

        try {
            // 6. 设置对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(contentType);
            metadata.setCacheControl("max-age=31536000"); // 缓存一年

            // 7. 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosConfig.getBucketName(),
                    filePath,
                    file.getInputStream(),
                    metadata
            );

            // 8. 执行上传
            PutObjectResult result = cosClient.putObject(putObjectRequest);
            log.info("文件上传成功，ETag: {}, 路径: {}", result.getETag(), filePath);

            // 9. 返回访问URL
            return generateAccessUrl(filePath);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    @Override
    public Boolean deleteFile(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return false;
        }

        try {
            // 从URL中提取文件路径
            String filePath = extractFilePathFromUrl(fileUrl);
            if (StringUtils.isBlank(filePath)) {
                return false;
            }

            // 删除文件
            cosClient.deleteObject(cosConfig.getBucketName(), filePath);
            log.info("文件删除成功，路径: {}", filePath);
            return true;

        } catch (Exception e) {
            log.error("文件删除失败，URL: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public String generateFilePath(Long userId, Long blogId, String title, String fileName) {
        // 清理标题，移除特殊字符
        String cleanTitle = cleanTitle(title);
        
        // 构建路径：/userId/blogId/title/fileName
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(userId);
        
        if (blogId != null) {
            pathBuilder.append("/").append(blogId);
        } else {
            pathBuilder.append("/temp"); // 临时目录，用于博客创建前的图片上传
        }
        
        pathBuilder.append("/").append(cleanTitle);
        pathBuilder.append("/").append(fileName);
        
        return pathBuilder.toString();
    }

    @Override
    public String copyFile(String sourceUrl, String targetPath) {
        if (StringUtils.isBlank(sourceUrl) || StringUtils.isBlank(targetPath)) {
            return null;
        }

        try {
            // 从源URL中提取源文件路径
            String sourcePath = extractFilePathFromUrl(sourceUrl);
            if (StringUtils.isBlank(sourcePath)) {
                log.error("无法从URL中提取源文件路径: {}", sourceUrl);
                return null;
            }

            // 创建复制请求
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                    cosConfig.getBucketName(), // 源桶
                    sourcePath,                 // 源文件路径
                    cosConfig.getBucketName(), // 目标桶
                    targetPath                  // 目标文件路径
            );

            // 执行复制
            CopyObjectResult result = cosClient.copyObject(copyObjectRequest);
            log.info("文件复制成功，从 {} 复制到 {}, ETag: {}", sourcePath, targetPath, result.getETag());

            // 删除源文件（可选）
            try {
                cosClient.deleteObject(cosConfig.getBucketName(), sourcePath);
                log.info("源文件删除成功: {}", sourcePath);
            } catch (Exception e) {
                log.warn("删除源文件失败: {}", sourcePath, e);
            }

            // 返回新的访问URL
            return generateAccessUrl(targetPath);

        } catch (Exception e) {
            log.error("文件复制失败，源URL: {}, 目标路径: {}", sourceUrl, targetPath, e);
            return null;
        }
    }

    /**
     * 生成唯一文件名（基于时间戳和UUID）
     */
    private String generateFileName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    /**
     * 清理标题，移除特殊字符
     */
    private String cleanTitle(String title) {
        if (StringUtils.isBlank(title)) {
            return "untitled";
        }
        
        // 移除特殊字符，只保留字母、数字、中文、下划线和短横线
        String cleaned = title.replaceAll("[^\\w\\u4e00-\\u9fa5-]", "_");
        
        // 限制长度
        if (cleaned.length() > 50) {
            cleaned = cleaned.substring(0, 50);
        }
        
        return cleaned;
    }

    /**
     * 生成访问URL
     */
    private String generateAccessUrl(String filePath) {
        if (StringUtils.isNotBlank(cosConfig.getDomain())) {
            // 使用自定义域名
            return cosConfig.getDomain() + "/" + filePath;
        } else {
            // 使用默认域名
            return String.format("https://%s.cos.%s.myqcloud.com/%s",
                    cosConfig.getBucketName(),
                    cosConfig.getRegion(),
                    filePath);
        }
    }

    /**
     * 从URL中提取文件路径
     */
    private String extractFilePathFromUrl(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return null;
        }

        try {
            // 如果是自定义域名
            if (StringUtils.isNotBlank(cosConfig.getDomain()) && fileUrl.startsWith(cosConfig.getDomain())) {
                return fileUrl.substring(cosConfig.getDomain().length() + 1);
            }

            // 如果是默认域名
            String defaultDomain = String.format("https://%s.cos.%s.myqcloud.com/",
                    cosConfig.getBucketName(),
                    cosConfig.getRegion());
            
            if (fileUrl.startsWith(defaultDomain)) {
                return fileUrl.substring(defaultDomain.length());
            }

            return null;
        } catch (Exception e) {
            log.error("提取文件路径失败，URL: {}", fileUrl, e);
            return null;
        }
    }
} 