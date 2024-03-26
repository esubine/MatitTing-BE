package com.kr.matitting.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.ResponseMyInfo;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PartyTeamRepository partyTeamRepository;
    private final JwtService jwtService;
    private final RedisUtil redisUtil;

    public User signUp(UserSignUpDto userSignUpDto) {
        User user = userRepository.findById(userSignUpDto.userId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        user.setNickname(userSignUpDto.nickname());
        user.setGender(userSignUpDto.gender());
        user.setAge(LocalDate.now().getYear() - userSignUpDto.birthday().getYear());
        user.setRole(Role.USER);

        return user;
    }

    public void update(User user, UserUpdateDto userUpdateDto) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        if (userUpdateDto.nickname() != null) findUser.setNickname(userUpdateDto.nickname());
        if (userUpdateDto.imgUrl() != null) findUser.setImgUrl(userUpdateDto.imgUrl());
    }

    public void logout(String accessToken) {
        log.info("=== logout() start ===");

        DecodedJWT decodedJWT = jwtService.isTokenValid(accessToken);
        String socialId = decodedJWT.getClaim("socialId").asString();

        //expired 시간 check
        tokenRemove(accessToken, socialId);
    }

    public void withdraw(String accessToken) {
        //TODO: 현재 로그인 사용자와 비교 필요
        log.info("=== withdraw() start ===");

        DecodedJWT decodedJWT = jwtService.isTokenValid(accessToken);
        String socialId = decodedJWT.getClaim("socialId").asString();

        tokenRemove(accessToken, socialId);

        User findUser = userRepository.findBySocialId(socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        userRepository.delete(findUser);
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

    public ResponseMyInfo getMyInfo(User user) {
        User myInfo = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        return new ResponseMyInfo(
                myInfo.getId(),
                myInfo.getSocialId(),
                myInfo.getOauthProvider(),
                myInfo.getEmail(),
                myInfo.getNickname(),
                myInfo.getAge(),
                myInfo.getImgUrl(),
                myInfo.getGender(),
                myInfo.getRole()
        );
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
            teams = partyTeamRepository.findByUserId(user.getId());
            parties = teams.stream().map(team -> team.getParty()).filter(party -> party.getStatus() == PartyStatus.PARTY_FINISH).map(party -> ResponsePartyDto.toDto(party)).sorted(Comparator.comparing(ResponsePartyDto::partyTime)).toList();
            return parties;
            }
        return new LinkedList<ResponsePartyDto>();
    }
}
