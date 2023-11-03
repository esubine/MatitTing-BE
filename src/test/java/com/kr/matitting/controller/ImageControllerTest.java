package com.kr.matitting.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 이미지_S3_업로드_실패() throws Exception {
        MockMultipartFile emptyImageFile = new MockMultipartFile("image", null, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image")
                        .file(emptyImageFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}