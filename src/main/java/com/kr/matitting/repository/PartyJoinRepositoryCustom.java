package com.kr.matitting.repository;

import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PartyJoinRepositoryCustom {
    Slice<PartyJoin> getPartyJoin(Pageable pageable, Long lastPartyJoinId, User user, Role role);
}