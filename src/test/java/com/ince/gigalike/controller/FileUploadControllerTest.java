package com.ince.gigalike.controller;

import com.ince.gigalike.service.FileUploadService;
import com.ince.gigalike.service.UserService;
import com.ince.gigalike.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.annotation.Resource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
public class FileUploadControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @MockBean
    private UserService userService;

    @Test
    public void testUploadAvatar() throws Exception {
        // 模拟用户
        User mockUser = new User();
        mockUser.setId(123456L);
        mockUser.setUsername("testuser");

        // 模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // 模拟服务方法
        when(userService.getLoginUser(any())).thenReturn(mockUser);
        when(fileUploadService.uploadUserAvatar(any(), eq(123456L)))
                .thenReturn("https://example.com/avatars/123456/20241201120000_a1b2c3d4.jpg");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file/upload/avatar")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("https://example.com/avatars/123456/20241201120000_a1b2c3d4.jpg"));
    }

    @Test
    public void testUploadBlogImage() throws Exception {
        // 模拟用户
        User mockUser = new User();
        mockUser.setId(123456L);
        mockUser.setUsername("testuser");

        // 模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "blog-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // 模拟服务方法
        when(userService.getLoginUser(any())).thenReturn(mockUser);
        when(fileUploadService.uploadBlogImage(any(), eq(123456L), any(), eq("Test Blog")))
                .thenReturn("https://example.com/123456/temp/Test_Blog/20241201120000_a1b2c3d4.jpg");

        // 执行测试
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file/upload/blog-image")
                        .file(file)
                        .param("title", "Test Blog")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("https://example.com/123456/temp/Test_Blog/20241201120000_a1b2c3d4.jpg"));
    }
} 