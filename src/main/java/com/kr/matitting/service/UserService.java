package com.kr.matitting.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final PartyTeamRepository partyTeamRepository;
    private final JwtService jwtService;
    private final RedisUtil redisUtil;

    public User signUp(UserSignUpDto userSignUpDto) {
        User user = userSignUpDto.toEntity();
        User save = userRepository.save(user);
        return save;
    }
    public void update(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        if (userUpdateDto.nickname() != null) {
            user.setNickname(userUpdateDto.nickname());
        }
        if (userUpdateDto.imgUrl() != null) {
            user.setImgUrl(userUpdateDto.imgUrl());
        }
    }

    public void logout(String accessToken) {
        log.info("=== logout() start ===");

        DecodedJWT decodedJWT = jwtService.isTokenValid(accessToken);
        String socialId = decodedJWT.getClaim("socialId").asString();

        //expired 시간 check
        Long expiration = jwtService.getExpiration(accessToken);

        //redis에서 로그아웃 user의 refreshToken을 삭제
        if (redisUtil.getData(socialId) != null) {
            redisUtil.deleteData(socialId);
        }
        redisUtil.setDateExpire(accessToken, "logout", expiration);
    }

    public void withdraw(String accessToken) {
        log.info("=== withdraw() start ===");

        DecodedJWT decodedJWT = jwtService.isTokenValid(accessToken);
        String socialId = decodedJWT.getClaim("socialId").asString();
        User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        userRepository.delete(user);
    }

    public User getMyInfo(SocialType socialType, String socialId) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        return user;
    }

    public List<ResponsePartyDto> getMyPartyList(User user, Role role) {
        List<ResponsePartyDto> parties;
        List<Team> teams;

        if (role == Role.HOST || role == Role.VOLUNTEER) {
            teams = partyTeamRepository.findByUserIdAndRole(user.getId(), role);
            parties = teams.stream().map(team -> team.getParty()).filter(party -> party.getStatus() != PartyStatus.PARTY_FINISH).map(party -> ResponsePartyDto.toDto(party)).sorted(Comparator.comparing(ResponsePartyDto::partyTime)).toList();
            return parties;
        }
        else if(role == Role.USER){
            teams = partyTeamRepository.findByUserId(user.getId() );
            parties = teams.stream().map(team -> team.getParty()).filter(party -> party.getStatus() == PartyStatus.PARTY_FINISH).map(party -> ResponsePartyDto.toDto(party)).sorted(Comparator.comparing(ResponsePartyDto::partyTime)).toList();
            return parties;
            }
        return new LinkedList<ResponsePartyDto>();
    }
}
