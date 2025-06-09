package com.ince.gigalike.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * 设备信息工具类
 */
public class DeviceUtils {
    
    /**
     * 获取设备信息
     */
    public static String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            return "未知设备";
        }
        
        // 移动设备检测
        if (isMobile(userAgent)) {
            if (userAgent.contains("iPhone")) {
                return "移动设备 - iPhone";
            } else if (userAgent.contains("Android")) {
                return "移动设备 - Android";
            } else if (userAgent.contains("iPad")) {
                return "移动设备 - iPad";
            } else {
                return "移动设备";
            }
        }
        
        // 桌面设备 - 浏览器检测
        String browser = getBrowserInfo(userAgent);
        return "桌面设备 - " + browser;
    }
    
    /**
     * 判断是否为移动设备
     */
    private static boolean isMobile(String userAgent) {
        String[] mobileKeywords = {
            "Mobile", "Android", "iPhone", "iPad", "Windows Phone",
            "BlackBerry", "Opera Mini", "IEMobile"
        };
        
        for (String keyword : mobileKeywords) {
            if (userAgent.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取浏览器信息
     */
    private static String getBrowserInfo(String userAgent) {
        if (userAgent.contains("Edg")) {
            return "Edge";
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else {
            return "其他浏览器";
        }
    }
} 