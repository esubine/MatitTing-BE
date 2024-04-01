package com.kr.matitting.service;

import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.ResponseMainPageDto;
import com.kr.matitting.dto.ResponseMainPartyListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.Party;
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
    private final double DEFAULT_LATITUDE = 37.566828706631135;
    private final double DEFAULT_LONGITUDE = 126.978646598009;
    private final PartyRepositoryImpl partyRepositoryImpl;

    public ResponseMainPageDto getPartyList(MainPageDto mainPageDto, Pageable pageable) {
        log.info("=== getPartyList() start ===");
        // 유저 위치 정보
        double userLat;
        double userLon;

        if(mainPageDto.latitude() == null || mainPageDto.longitude() == null){
            userLat = DEFAULT_LATITUDE;
            userLon = DEFAULT_LONGITUDE;
        } else {
            userLat = mainPageDto.latitude();
            userLon = mainPageDto.longitude();
        }

        Slice<Party> partyList = partyRepositoryImpl.getPartyList(userLat, userLon, mainPageDto.partyStatus(), mainPageDto.sort(), mainPageDto.lastPartyId(), pageable);

        List<ResponseMainPartyListDto> responsePartyList = partyList.stream()
                .map(ResponseMainPartyListDto::toDto)
                .collect(Collectors.toList());

        Long newLastPartyId = getLastPartyId(responsePartyList);
        ResponsePageInfoDto responsePageInfoDto = getPageInfo(partyList, newLastPartyId);

        return new ResponseMainPageDto(responsePartyList, responsePageInfoDto);
    }

    private Long getLastPartyId(List<ResponseMainPartyListDto> responsePartyList){
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
