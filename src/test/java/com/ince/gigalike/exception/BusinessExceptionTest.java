package com.ince.gigalike.exception;

import com.ince.gigalike.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BusinessException测试类
 */
public class BusinessExceptionTest {

    @Test
    public void testBusinessExceptionWithErrorCodeOnly() {
        // 测试只传入ErrorCode的构造函数
        BusinessException exception = new BusinessException(ErrorCode.PARAMS_ERROR);
        
        assertEquals(40000, exception.getCode());
        assertEquals("请求参数错误", exception.getMessage());
        assertEquals("请求参数错误", exception.getDescription());
    }

    @Test
    public void testBusinessExceptionWithCustomDescription() {
        // 测试传入ErrorCode和自定义描述的构造函数
        String customMessage = "用户名长度必须在4-20个字符之间";
        BusinessException exception = new BusinessException(ErrorCode.PARAMS_ERROR, customMessage);
        
        assertEquals(40000, exception.getCode());
        assertEquals(customMessage, exception.getMessage()); // 应该返回自定义消息
        assertEquals(customMessage, exception.getDescription());
    }

    @Test
    public void testBusinessExceptionWithAllParameters() {
        // 测试传入所有参数的构造函数
        String message = "自定义异常消息";
        int code = 40000;
        String description = "详细描述";
        
        BusinessException exception = new BusinessException(message, code, description);
        
        assertEquals(code, exception.getCode());
        assertEquals(message, exception.getMessage());
        assertEquals(description, exception.getDescription());
    }
} 