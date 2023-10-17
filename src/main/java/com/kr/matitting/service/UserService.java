package com.kr.matitting.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.team.TeamException;
import com.kr.matitting.exception.team.TeamExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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

    @Transactional
    public void update(UserUpdateDto userUpdateDto) {
        Optional<User> user = userRepository.findById(userUpdateDto.userId());
        String answer = "";
        if (!userUpdateDto.nickname().isEmpty()) {
            user.get().setNickname(userUpdateDto.nickname().get());
        }
        if (!userUpdateDto.imgUrl().isEmpty()) {
            user.get().setImgUrl(userUpdateDto.imgUrl().get());
        }
    }

    public void logout(String accessToken) {
        log.info("=== logout() start ===");

        DecodedJWT decodedJWT = jwtService.isTokenValid(accessToken);
        String socialId = decodedJWT.getClaim("id").asString();

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
        String socialId = decodedJWT.getClaim("id").asString();
        User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        userRepository.delete(user);
    }

    public User getMyInfo(SocialType socialType, String socialId) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        return user;
    }

    public List<PartyCreateDto> getMyPartyList(Long userId, Role role) {
        List<PartyCreateDto> parties;
        if (role == Role.HOST || role == Role.VOLUNTEER) {
            parties = partyTeamRepository.findByUserIdAndRole(userId, role)
                    .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM))
                    .stream().map(team -> team.getParty()).map(party -> party.toDto(party)).sorted(Comparator.comparing(PartyCreateDto::getPartyTime)).toList();
        }
        else{
            parties = partyTeamRepository.findByUserId(userId)
                    .orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM))
                    .stream().map(team -> team.getParty()).map(party -> party.toDto(party)).sorted(Comparator.comparing(PartyCreateDto::getPartyTime)).toList();
        }
        return parties;
    }
}
