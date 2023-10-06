package com.kr.matitting.service;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.repository.PartyRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {
//    private final PartyRepositoryCustom partyRepositoryCustom;
//
//    public Page<Party> getPartyPage(PartySearchCondDto partySearchCondDto, Pageable pageable) {
//        return partyRepositoryCustom.searchPage(partySearchCondDto, pageable);
//    }
}
