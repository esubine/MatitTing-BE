package com.kr.matitting.service;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.ResponseMyInfo;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
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
import java.util.ArrayList;
import java.util.Comparator;
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
        if (userUpdateDto.nickname() != null) user.setNickname(userUpdateDto.nickname());
        if (userUpdateDto.imgUrl() != null) user.setImgUrl(userUpdateDto.imgUrl());
    }

    public void logout(String accessToken, User user) {
        log.info("=== logout() start ===");

        jwtService.isTokenValid(accessToken);
        //expired 시간 check
        tokenRemove(accessToken, user.getSocialId());
    }


    public void withdraw(String accessToken, User user) {
        log.info("=== withdraw() start ===");

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

    public ResponseMyInfo getMyInfo(User user) {
        return new ResponseMyInfo(
                user.getId(),
                user.getSocialId(),
                user.getOauthProvider(),
                user.getEmail(),
                user.getNickname(),
                user.getAge(),
                user.getImgUrl(),
                user.getGender(),
                user.getRole()
        );
    }

    public List<ResponsePartyDto> getMyPartyList(User user, Role role) {
        List<Team> teams = new ArrayList<>();

        if (role == Role.HOST || role == Role.VOLUNTEER) {
            teams = partyTeamRepository.findByUserIdAndRole(user.getId(), role);
        } else if (role == Role.USER) {
            teams = partyTeamRepository.findByUserId(user.getId());
        }
        return teams.stream()
                .map(Team::getParty)
                .filter(party -> role == Role.USER ? party.getStatus() == PartyStatus.PARTY_FINISH : party.getStatus() != PartyStatus.PARTY_FINISH)
                .map(ResponsePartyDto::toDto)
                .sorted(Comparator.comparing(ResponsePartyDto::partyTime))
                .toList();
    }
}
