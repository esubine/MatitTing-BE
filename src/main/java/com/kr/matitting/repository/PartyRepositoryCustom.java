package com.kr.matitting.repository;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyRepositoryCustom {
    Page<Party> searchPage(PartySearchCondDto partySearchCondDto, Pageable pageable);
}
