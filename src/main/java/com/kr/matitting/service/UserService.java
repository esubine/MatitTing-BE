package com.kr.matitting.service;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.repository.PartyRepositoryImpl;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final EntityFacade entityFacade;
    private final UserRepository userRepository;
    private final PartyRepositoryImpl partyRepository;
    private final JwtService jwtService;
    private final RedisUtil redisUtil;

    public User signUp(UserSignUpDto userSignUpDto) {
        User user = entityFacade.getUser(userSignUpDto.userId());
        user.setNickname(userSignUpDto.nickname());
        user.setGender(userSignUpDto.gender());
        user.setAge(LocalDate.now().getYear() - userSignUpDto.birthday().getYear());
        user.setRole(Role.USER);

        return user;
    }

    public void update(Long userId, UserUpdateDto userUpdateDto) {
        User user = entityFacade.getUser(userId);
        if (userUpdateDto.nickname() != null) user.setNickname(userUpdateDto.nickname());
        if (userUpdateDto.imgUrl() != null) user.setImgUrl(userUpdateDto.imgUrl());
    }

    public void logout(String accessToken, Long userId) {
        log.debug("=== logout() start ===");

        User user = entityFacade.getUser(userId);
        jwtService.isTokenValid(accessToken);
        //expired 시간 check
        tokenRemove(accessToken, user.getSocialId());
    }


    public void withdraw(String accessToken, Long userId) {
        log.info("=== withdraw() start ===");

        User user = entityFacade.getUser(userId);
        tokenRemove(accessToken, user.getSocialId());
        userRepository.delete(user);
    }

    private void tokenRemove(String accessToken, String socialId) {
        //expired 시간 check
        Long expiration = jwtService.getExpiration(accessToken);

        //redis에서 로그아웃 user의 refreshToken을 삭제
        if (redisUtil.getData(socialId) != null) {
            redisUtil.deleteData(socialId);
        }
        redisUtil.setDateExpire(accessToken, "logout", expiration);
    }

    public ResponseMyInfo getMyInfo(Long userId) {
        User user = entityFacade.getUser(userId);
        return ResponseMyInfo.toDto(user);
    }

    public ResponseMyParty getMyPartyList(Long userId, PartyStatusReq partyStatusReq, Pageable pageable) {
        User user = entityFacade.getUser(userId);

        Page<Party> myParty = partyRepository.getMyParty(user, partyStatusReq, pageable);
        List<ResponsePartyDto> responsePartyDtos = myParty.stream()
                .map(party -> {
                    List<Long> userIdList = Optional.ofNullable(party.getReviews())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(review -> review.getReviewer().getId())
                            .toList();
                    boolean reviewExist = userIdList.contains(user.getId());
                    return ResponsePartyDto.toDto(party, reviewExist);
                })
                .toList();

        return new ResponseMyParty(responsePartyDtos, new ResponsePageInfoDto(pageable.getPageNumber(), myParty.hasNext()));
    }
}
