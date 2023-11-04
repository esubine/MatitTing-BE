package com.kr.matitting.service;

import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.exception.Map.MapException;
import com.kr.matitting.exception.Map.MapExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class MapServiceTest {
    @Autowired
    private MapService mapService;
    public static double TEST_LONGITUDE = 126.978646598009;
    public static double TEST_LATITUDE = 37.566828706631135;

    public static String TEST_ADDRESS = "서울특별시 중구 세종대로 110";

    @Test
    void 주소변환_성공() {
        //given
        MainPageDto mainPageDto = new MainPageDto(TEST_LONGITUDE, TEST_LATITUDE);

        //when
        String address = mapService.coordToAddr(mainPageDto.getLongitude(), mainPageDto.getLatitude());

        //Then
        assertThat(address).isEqualTo(TEST_ADDRESS);

    }

    @Test
    void 주소변환_실패() {

        MapService mapService = mock(MapService.class);
        //given
        MainPageDto mainPageDto = new MainPageDto(0.0, 0.0);

        //when, Then
        String address = mapService.coordToAddr(mainPageDto.getLongitude(), mainPageDto.getLatitude());

        when(address == null)
                .thenThrow(new MapException(MapExceptionType.NOT_FOUND_ADDRESS));

        assertThrows(MapException.class, () -> {
            mapService.coordToAddr(mainPageDto.getLongitude(), mainPageDto.getLatitude());
        });
    }

}