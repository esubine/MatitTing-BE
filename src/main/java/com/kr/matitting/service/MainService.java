package com.kr.matitting.service;

import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.ResponseMainPageDto;
import com.kr.matitting.dto.ResponseMainPartyListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.repository.PartyRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MainService {
    private final PartyRepositoryImpl partyRepositoryImpl;

    public ResponseMainPageDto getPartyList(MainPageDto mainPageDto, Pageable pageable) {
        log.info("=== getPartyList() start ===");

        Page<Party> parties = partyRepositoryImpl.mainPage(mainPageDto, pageable);
        return new ResponseMainPageDto(parties.stream().map(ResponseMainPartyListDto::toDto).toList(), new ResponsePageInfoDto(pageable.getPageNumber(), parties.hasNext()));
    }
}
