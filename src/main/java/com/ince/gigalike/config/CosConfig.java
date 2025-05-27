package com.ince.gigalike.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置
 */
@Configuration
@ConfigurationProperties(prefix = "cos")
@Data
public class CosConfig {

    /**
     * 腾讯云SecretId
     */
    private String secretId;

    /**
     * 腾讯云SecretKey
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName = "gigalike-1307454348";

    /**
     * 地域
     */
    private String region = "ap-guangzhou";

    /**
     * 访问域名
     */
    private String domain;

    @Bean
    public COSClient cosClient() {
        // 1 初始化用户身份信息（secretId, secretKey）
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        
        // 2 设置 bucket 的地域
        Region regionObj = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionObj);
        
        // 3 设置超时时间为4秒（4000毫秒）
        clientConfig.setConnectionTimeout(4000);  // 连接超时时间
        clientConfig.setSocketTimeout(4000);      // 读取超时时间
        clientConfig.setConnectionRequestTimeout(4000); // 从连接池获取连接的超时时间
        
        // 4 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }
} 