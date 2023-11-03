package com.kr.matitting.controller;

import com.kr.matitting.dto.MainPageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void 메인페이지_조회_성공() throws Exception {
        MainPageDto mainPageDto = new MainPageDto(37.566828706631135,126.978646598009);
        Pageable pageable = PageRequest.of(0, 10);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/main/")
                        .param("latitude", String.valueOf(mainPageDto.getLatitude()))
                        .param("longitude", String.valueOf(mainPageDto.getLongitude()))
                        .param("offset", String.valueOf(pageable.getOffset()))
                        .param("limit", String.valueOf(pageable.getPageSize())))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}