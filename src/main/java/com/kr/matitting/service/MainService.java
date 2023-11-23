package com.kr.matitting.service;

import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.exception.Map.MapException;
import com.kr.matitting.repository.PartyRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MainService {
    private final int MAX_DISTANCE = 20;
    private final double DEFAULT_LATITUDE = 37.566828706631135;
    private final double DEFAULT_LONGITUDE = 126.978646598009;
    private final PartyRepositoryImpl partyRepositoryImpl;

    public ResponseMainPageDto getPartyList(MainPageDto mainPageDto, Pageable pageable, Long lastPartyId) {
        log.info("=== getPartyList() start ===");
        CalculateDto calculateDto;

        if (mainPageDto.getLatitude() == null | mainPageDto.getLongitude() == null) {
            calculateDto = calculate(DEFAULT_LONGITUDE, DEFAULT_LATITUDE);
        } else {
            calculateDto = calculate(mainPageDto.getLongitude(), mainPageDto.getLatitude());
        }

        if (calculateDto.getMinLatitude() < 0 || calculateDto.getMaxLatitude() < 0
                || calculateDto.getMinLongitude() < 0 || calculateDto.getMaxLongitude() < 0) {
            throw new MapException(com.kr.matitting.exception.main.MainExceptionType.INVALID_COORDINATE);
        }

        Slice<Party> partyList = partyRepositoryImpl.getPartyList(calculateDto.getMinLatitude(), calculateDto.getMaxLatitude(),
                                                                calculateDto.getMinLongitude(), calculateDto.getMaxLongitude(),
                                                                pageable, lastPartyId);

        List<ResponsePartyDto> responsePartyList = partyList.stream()
                .map(ResponsePartyDto::toDto)
                .collect(Collectors.toList());

        Long newLastPartyId = getLastPartyId(responsePartyList);
        ResponsePageInfoDto responsePageInfoDto = getPageInfo(partyList, newLastPartyId);

        return new ResponseMainPageDto(responsePartyList, responsePageInfoDto);
    }

    // 유저의 위도, 경도를 바탕으로 반경 10km 위도, 경도값 계산
    private CalculateDto calculate(double userLongitude, double userLatitude) {
        double earthRadius = 6371; // 지구 반지름 (단위: km)

        double minLat = userLatitude - (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        double maxLat = userLatitude + (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        double minLon = userLongitude - (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        double maxLon = userLongitude + (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);

        return new CalculateDto(minLat, maxLat, minLon, maxLon);
    }

    private Long getLastPartyId(List<ResponsePartyDto> responsePartyList){
        Long id;
        if(responsePartyList.isEmpty()){
            id = null;
        } else{
            id = responsePartyList.get(responsePartyList.size() - 1).partyId();
        }
        return id;
    }

    private ResponsePageInfoDto getPageInfo(Slice<Party> partyList, Long newLastPartyId) {
        boolean hasNext = partyList.hasNext();

        return new ResponsePageInfoDto(newLastPartyId, hasNext);
    }
}
