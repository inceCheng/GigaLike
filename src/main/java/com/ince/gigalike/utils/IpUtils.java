package com.ince.gigalike.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpUtils {
    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";
    private static Searcher searcher;

    static {
        try {
            // 从classpath加载ip2region.xdb文件
            ClassPathResource resource = new ClassPathResource("ip2region.xdb");
            InputStream inputStream = resource.getInputStream();
            byte[] cBuff = FileCopyUtils.copyToByteArray(inputStream);
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            logger.error("Failed to create searcher: " + e.getMessage());
        }
    }

    /**
     * 获取IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        try {
            // 打印所有请求头，用于调试
            logger.info("=== Request Headers ===");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                logger.info("{}: {}", headerName, headerValue);
            }
            logger.info("====================");

            // 1. 优先获取 natapp 的真实IP
            ipAddress = request.getHeader("X-Natapp-IP");
            logger.info("X-Natapp-IP: {}", ipAddress);

            // 2. 如果没有 natapp IP，尝试获取X-Real-IP
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Real-IP");
                logger.info("X-Real-IP: {}", ipAddress);
            }

            // 3. 如果还是没有，尝试X-Forwarded-For
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                String forwardedFor = request.getHeader("X-Forwarded-For");
                logger.info("X-Forwarded-For: {}", forwardedFor);
                if (StringUtils.isNotBlank(forwardedFor)) {
                    String[] ips = forwardedFor.split(",");
                    ipAddress = ips[0].trim();
                }
            }

            // 4. 如果还是没有，尝试其他的header
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
                logger.info("Proxy-Client-IP: {}", ipAddress);
            }
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
                logger.info("WL-Proxy-Client-IP: {}", ipAddress);
            }
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
                logger.info("HTTP_X_FORWARDED_FOR: {}", ipAddress);
            }
            
            // 5. 最后才使用RemoteAddr
            if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                logger.info("RemoteAddr: {}", ipAddress);
                
                // 如果是本地访问，则获取本机真实IP
                if (LOCALHOST.equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                    try {
                        InetAddress inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                        logger.info("Local IP: {}", ipAddress);
                    } catch (UnknownHostException e) {
                        logger.error("Failed to get local host: {}", e.getMessage());
                    }
                }
            }

            // 6. 处理特殊情况
            if (StringUtils.isBlank(ipAddress) 
                || UNKNOWN.equalsIgnoreCase(ipAddress)
                || LOCALHOST.equals(ipAddress)
                || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                ipAddress = "127.0.0.1";
            }
            
            logger.info("Final IP Address: {}", ipAddress);
            
        } catch (Exception e) {
            logger.error("Error getting IP address: {}", e.getMessage());
            ipAddress = "127.0.0.1";
        }
        
        return ipAddress;
    }

    /**
     * 获取IP归属地
     */
    public static String getIpLocation(String ip) {
        try {
            if (searcher == null || StringUtils.isBlank(ip)) {
                return "未知";
            }

            // 检查是否为本地IP
            if (LOCALHOST.equals(ip) || "localhost".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                return "本地";
            }

            String region = searcher.search(ip);
            if (StringUtils.isBlank(region)) {
                return "未知";
            }

            // region格式：国家|区域|省份|城市|ISP
            String[] splitRegion = region.split("\\|");
            
            // 处理中国的地址
            if ("中国".equals(splitRegion[0]) || "0".equals(splitRegion[0])) {
                String province = splitRegion[2];
                if (StringUtils.isNotBlank(province) && !"0".equals(province)) {
                    return province;
                }
            }
            // 国外地址仅显示国家
            else if (StringUtils.isNotBlank(splitRegion[0]) && !"0".equals(splitRegion[0])) {
                return splitRegion[0];
            }

            return "未知";
        } catch (Exception e) {
            System.out.println("Failed to search ip location: " + e.getMessage());
            return "未知";
        }
    }
} 