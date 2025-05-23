package com.ince.gigalike.controller;

import com.ince.gigalike.common.BaseResponse;
import com.ince.gigalike.model.dto.DoThumbRequest;
import com.ince.gigalike.service.ThumbService;
import com.ince.gigalike.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("thumb")
public class ThumbController {
    @Resource
    private ThumbService thumbService;

    /**
     * 点赞
     *
     * @param doThumbRequest
     * @param request
     * @return
     */
    @PostMapping("/do")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) throws PulsarClientException {
        Boolean success = thumbService.doThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }

    /**
     * 取消点赞
     *
     * @param doThumbRequest
     * @param request
     * @return
     */
    @PostMapping("/undo")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) throws PulsarClientException {
        Boolean success = thumbService.undoThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }

}
