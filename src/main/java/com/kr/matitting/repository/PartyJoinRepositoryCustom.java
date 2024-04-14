package com.kr.matitting.repository;

import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyJoinRepositoryCustom {
    Page<PartyJoin> getPartyJoin(Pageable pageable, User user, Role role);
}