package com.ince.gigalike.service;

import com.ince.gigalike.model.dto.DoThumbRequest;
import com.ince.gigalike.model.entity.Thumb;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author inceCheng
* @description 针对表【thumb】的数据库操作Service
* @createDate 2025-05-14 13:55:04
*/
public interface ThumbService extends IService<Thumb> {

    /**
     * 点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 取消点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

}
