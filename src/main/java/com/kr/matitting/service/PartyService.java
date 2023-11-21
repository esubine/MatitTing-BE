package com.kr.matitting.service;

import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyJoinStatus;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.Map.MapException;
import com.kr.matitting.exception.Map.MapExceptionType;
import com.kr.matitting.exception.main.MainExceptionType;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.partyjoin.PartyJoinException;
import com.kr.matitting.exception.partyjoin.PartyJoinExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.repository.PartyRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kr.matitting.dto.ChatRoomDto.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {
    @Value("${cloud.aws.s3.url}")
    private String url;
    private final ApplicationEventPublisher eventPublisher;
    private final PartyJoinRepository partyJoinRepository;
    private final PartyTeamRepository teamRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final MapService mapService;

    public ResponsePartyDto getPartyInfo(Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        ResponsePartyDto responsePartyDto = ResponsePartyDto.toDto(party);
        return responsePartyDto;
    }

    public Map<String, Long> createParty(PartyCreateDto request) {
        log.info("=== createParty() start ===");

        Long userId = request.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        checkParticipant(request.getTotalParticipant());

        if (request.getDeadline() != null) {
            checkTime(request.getPartyTime(), request.getDeadline());
        }

        // address 변환, deadline, thumbnail이 null일 경우 처리하는 로직 처리 후 생성
        Party party = createBasePartyBuilder(request, user);
        Party savedParty = partyRepository.save(party);

        Team team = Team.builder()
                .user(user)
                .party(savedParty)
                .role(Role.HOST)
                .build();

        teamRepository.save(team);

        Map<String, Long> partyId = new HashMap<>();
        partyId.put("partyId", savedParty.getId());

        eventPublisher.publishEvent(new CreateRoomEvent(savedParty.getId(), user.getId()));

        return partyId;

    }

    private Point2D.Double setLocationFunc(double latitude, double longitude) {
        Point2D.Double now = new Point2D.Double();
        now.setLocation(latitude, longitude);
        return now;
    }

    private void checkParticipant(int totalParticipant) {
        if (totalParticipant < 2) {
            throw new PartyException(PartyExceptionType.NOT_MINIMUM_PARTICIPANT);
        }
    }

    private void checkTime(LocalDateTime partyTime, LocalDateTime deadline) {
        if (partyTime.isBefore(deadline)) {
            throw new PartyException(PartyExceptionType.WRONG_TIME);
        }
    }

    private String getThumbnail(PartyCategory category, String thumbnail) {

        if (thumbnail == null) {
            switch (category) {
                case KOREAN -> thumbnail = url + "korean.jpeg";
                case WESTERN -> thumbnail = url + "western.jpeg";
                case CHINESE -> thumbnail = url + "chinese.jpeg";
                case JAPANESE -> thumbnail = url + "japanese.jpeg";
                case ETC -> thumbnail = url + "etc.jpeg";
            }
        }
        return thumbnail;
    }

    private LocalDateTime getDeadline(LocalDateTime deadline, LocalDateTime partyTime) {
        if (deadline == null) {
            deadline = partyTime.minusHours(1L);
        }
        return deadline;
    }

    private String getAddress(double longitude, double latitude) {
        String address = mapService.coordToAddr(longitude, latitude);
        if (address.equals("")) {
            throw new MapException(MapExceptionType.NOT_FOUND_ADDRESS);
        }
        return address;
    }

    public void partyUpdate(PartyUpdateDto partyUpdateDto, Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        if (partyUpdateDto.partyTitle() != null) {
            party.setPartyTitle(partyUpdateDto.partyTitle());
        }
        if (partyUpdateDto.partyContent() != null) {
            party.setPartyContent(partyUpdateDto.partyContent());
        }
        if (partyUpdateDto.menu() != null) {
            party.setMenu(partyUpdateDto.menu());
        }
        if (partyUpdateDto.longitude() != null && partyUpdateDto.latitude() != null) {
            party.setLatitude(partyUpdateDto.latitude());
            party.setLongitude(partyUpdateDto.longitude());
            String address = mapService.coordToAddr(partyUpdateDto.longitude(), partyUpdateDto.latitude());
            party.setAddress(address);
        }
        if (partyUpdateDto.status() != null) {
            party.setStatus(partyUpdateDto.status());
        }
        if (partyUpdateDto.thumbnail() != null) {
            party.setThumbnail(partyUpdateDto.thumbnail());
        }
        if (partyUpdateDto.deadline() != null) {
            if (timeValidCheck(partyUpdateDto.deadline(), party.getPartyTime())) {
                party.setDeadline(partyUpdateDto.deadline());
            }
        }
        if (partyUpdateDto.partyTime() != null) {
            if (timeValidCheck(party.getDeadline(), partyUpdateDto.partyTime())) {
                party.setPartyTime(partyUpdateDto.partyTime());
            }
        }
        if (partyUpdateDto.totalParticipant() != null) {
            if (party.getParticipantCount() <= partyUpdateDto.totalParticipant()) {
                party.setTotalParticipant(partyUpdateDto.totalParticipant());
            } else {
                throw new PartyException(PartyExceptionType.INVALID_UPDATE_VALUE);
            }
        }
        if (partyUpdateDto.gender() != null) {
            party.setGender(partyUpdateDto.gender());
        }
        if (partyUpdateDto.age() != null) {
            party.setAge(partyUpdateDto.age());
        }
    }

    private boolean timeValidCheck(LocalDateTime deadlineTime, LocalDateTime partyTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(deadlineTime) && deadlineTime.isBefore(partyTime)) {
            return true;
        } else {
            throw new PartyException(PartyExceptionType.INVALID_UPDATE_VALUE);
        }
    }

    public void deleteParty(Long partyId) {
        List<PartyJoin> partyJoinList = partyJoinRepository.findByPartyId(partyId);
        partyJoinList.stream().forEach(partyJoin -> partyJoinRepository.delete(partyJoin));
        List<Team> teamList = teamRepository.findByPartyId(partyId);
        teamList.stream().forEach(team -> teamRepository.delete(team));
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        partyRepository.delete(party);
    }

    // address, deadline, thumbnail와 같이 변환이나 null인 경우 처리가 필요한 필드는 제외하고 나머지 필드는 빌더패턴으로 생성
    private Party createBasePartyBuilder(PartyCreateDto request, User user) {
        return Party.builder()
                .partyTitle(request.getTitle())
                .partyContent(request.getContent())
                .latitude(setLocationFunc(request.getLatitude(), request.getLongitude()).x)
                .longitude(setLocationFunc(request.getLatitude(), request.getLongitude()).y)
                .partyTime(request.getPartyTime())
                .totalParticipant(request.getTotalParticipant())
                .gender(request.getGender())
                .age(request.getAge())
                .menu(request.getMenu())
                .category(request.getCategory())
                .status(PartyStatus.RECRUIT)
                .user(user)
                .address(getAddress(request.getLongitude(), request.getLatitude()))
                .deadline(getDeadline(request.getDeadline(), request.getPartyTime()))
                .thumbnail(getThumbnail(request.getCategory(), request.getThumbnail()))
                .build();
    }

    public void joinParty(PartyJoinDto partyJoinDto, User user) {
        log.info("=== joinParty() start ===");

        Party party = partyRepository.findById(partyJoinDto.partyId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
        if (party.getUser().getId() != partyJoinDto.leaderId()) {
            throw new UserException(UserExceptionType.NOT_FOUND_USER);
        }
        PartyJoin partyJoin = PartyJoin.builder().party(party).leaderId(partyJoinDto.leaderId()).userId(user.getId()).build();
        partyJoinRepository.save(partyJoin);
    }

    public String decideUser(PartyJoinDto partyJoinDto, User user) {
        log.info("=== decideUser() start ===");

        if (!(partyJoinDto.status() == PartyJoinStatus.ACCEPT || partyJoinDto.status() == PartyJoinStatus.REFUSE)) {
            log.error("=== Party Join Status was requested incorrectly ===");
            throw new PartyJoinException(PartyJoinExceptionType.WRONG_STATUS);
        }

        PartyJoin findPartyJoin = partyJoinRepository.findByPartyIdAndLeaderIdAndUserId(
                partyJoinDto.partyId(),
                partyJoinDto.leaderId(),
                user.getId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
        partyJoinRepository.delete(findPartyJoin);

        if (partyJoinDto.status() == PartyJoinStatus.ACCEPT) {
            log.info("=== ACCEPT ===");
            Party party = partyRepository.findById(partyJoinDto.partyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
            party.increaseUser();
            Team member = Team.builder().user(user).party(party).role(Role.VOLUNTEER).build();
            teamRepository.save(member);
            return "Accept Request Completed";
        } else if (partyJoinDto.status() == PartyJoinStatus.REFUSE) {
            log.info("=== REFUSE ===");
            return "Refuse Request Completed";
        }
        return null;
    }
}
