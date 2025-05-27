package com.ince.gigalike.controller;

import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.enums.ErrorCode;
import com.ince.gigalike.exception.BusinessException;
import com.ince.gigalike.model.dto.EmailCodeRequest;
import com.ince.gigalike.service.EmailService;
import com.ince.gigalike.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@Tag(name = "邮箱验证码接口", description = "邮箱验证码相关操作")
public class EmailController {
    
    @Resource
    private EmailService emailService;
    
    @PostMapping("/send-code")
    @Operation(summary = "发送邮箱验证码", description = "发送邮箱验证码，无需图形验证码")
    public BaseResponse<String> sendEmailCode(@RequestBody @Valid EmailCodeRequest emailCodeRequest) {
        boolean success = emailService.sendEmailCode(emailCodeRequest.getEmail());
        
        if (success) {
            return ResultUtils.success("邮箱验证码发送成功");
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱验证码发送失败");
        }
    }
} 