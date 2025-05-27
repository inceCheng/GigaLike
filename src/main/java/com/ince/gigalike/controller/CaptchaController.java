package com.ince.gigalike.controller;

import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.dto.CaptchaResponse;
import com.ince.gigalike.service.CaptchaService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码接口", description = "验证码相关操作")
public class CaptchaController {
    
    @Resource
    private CaptchaService captchaService;
    
    @GetMapping("/generate")
    @Operation(summary = "生成验证码", description = "生成图形验证码，返回验证码ID和Base64图片")
    public BaseResponse<CaptchaResponse> generateCaptcha() {
        CaptchaResponse captchaResponse = captchaService.generateCaptcha();
        return ResultUtils.success(captchaResponse);
    }
} 