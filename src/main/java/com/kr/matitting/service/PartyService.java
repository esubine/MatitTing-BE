package com.kr.matitting.service;

import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyJoinStatus;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.CalculateDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {
    @Value("${cloud.aws.s3.url}")
    private String url;

    private final int MAX_DISTANCE = 20;

    private final PartyJoinRepository partyJoinRepository;
    private final PartyTeamRepository teamRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final PartyRepositoryImpl partyRepositoryImpl;
    private final MapService mapService;

    public ResponsePartyDto getPartyInfo(Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        ResponsePartyDto responsePartyDto = ResponsePartyDto.toDto(party);
        return responsePartyDto;
    }

    public Map<String, Long> createParty(PartyCreateDto request) {
        log.info("=== createParty() start ===");

        Long user_id = request.getUser_id();
        User user = userRepository.findById(user_id).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        // address 변환, deadline, thumbnail이 null일 경우 처리하는 로직 처리 후 생성
        Party party = createBasePartyBuilder(request, user);
        Party savedParty = partyRepository.save(party);

        Map<String, Long> partyId = new HashMap<>();
        partyId.put("partyId", savedParty.getId());

        return partyId;

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
        return address;
    }

    public void partyUpdate(PartyUpdateDto partyUpdateDto) {
        Party party = partyRepository.findById(partyUpdateDto.partyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        if (!partyUpdateDto.partyTitle().isEmpty()) {
            party.setPartyTitle(partyUpdateDto.partyTitle().get());
        }
        if (!partyUpdateDto.partyContent().isEmpty()) {
            party.setPartyContent(partyUpdateDto.partyContent().get());
        }
        if (!partyUpdateDto.menu().isEmpty()) {
            party.setMenu(partyUpdateDto.menu().get());
        }
        if (!partyUpdateDto.longitude().isEmpty() && !partyUpdateDto.latitude().isEmpty()) {
            mapService.coordToAddr(partyUpdateDto.longitude().get(), partyUpdateDto.latitude().get());
        }
        if (!partyUpdateDto.status().isEmpty()) {
            party.setStatus(partyUpdateDto.status().get());
        }
        if (!partyUpdateDto.thumbnail().isEmpty()) {
            party.setThumbnail(partyUpdateDto.thumbnail().get());
        }
        if (!partyUpdateDto.deadline().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadlineTime = partyUpdateDto.deadline().get();
            if (now.isBefore(deadlineTime) && party.getPartyTime().isBefore(deadlineTime)) {
                party.setDeadline(deadlineTime);
            }
        }
        if (!partyUpdateDto.totalParticipant().isEmpty()) {
            if (party.getParticipantCount() <= partyUpdateDto.totalParticipant().get()) {
                party.setTotalParticipant(partyUpdateDto.totalParticipant().get());
            }
        }
        if (!partyUpdateDto.gender().isEmpty()) {
            party.setGender(partyUpdateDto.gender().get());
        }
    }

    public void deleteParty(Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        partyRepository.delete(party);
    }

    // address, deadline, thumbnail와 같이 변환이나 null인 경우 처리가 필요한 필드는 제외하고 나머지 필드는 빌더패턴으로 생성
    private Party createBasePartyBuilder(PartyCreateDto request, User user) {
        return Party.builder()
                .partyTitle(request.getTitle())
                .partyContent(request.getContent())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
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

    public void joinParty(PartyJoinDto partyJoinDto) {
        log.info("=== joinParty() start ===");

        if (partyJoinDto.partyId() == null ||
                partyJoinDto.leaderId() == null ||
                partyJoinDto.userId() == null) {
            log.error("=== JoinParty:Request Data is null ===");
            throw new PartyJoinException(PartyJoinExceptionType.NULL_POINT_PARTY_JOIN);
        }

        Party party = partyRepository.findById(partyJoinDto.partyId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
        PartyJoin partyJoin = PartyJoin.builder().party(party).leaderId(partyJoinDto.leaderId()).userId(partyJoinDto.userId()).build();
        partyJoinRepository.save(partyJoin);
    }

    public String decideUser(PartyJoinDto partyJoinDto) {
        log.info("=== decideUser() start ===");

        if (!(partyJoinDto.status().get() == PartyJoinStatus.ACCEPT || partyJoinDto.status().get() == PartyJoinStatus.REFUSE)) {
            log.error("=== Party Join Status was requested incorrectly ===");
            throw new PartyJoinException(PartyJoinExceptionType.WRONG_STATUS);
        }

        PartyJoin findPartyJoin = partyJoinRepository.findByPartyIdAndLeaderIdAndUserId(
                partyJoinDto.partyId(),
                partyJoinDto.leaderId(),
                partyJoinDto.userId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
        partyJoinRepository.delete(findPartyJoin);

        if (partyJoinDto.status().get() == PartyJoinStatus.ACCEPT) {
            log.info("=== ACCEPT ===");
            User user = userRepository.findById(partyJoinDto.userId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
            Party party = partyRepository.findById(partyJoinDto.partyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
            Team member = Team.builder().user(user).party(party).role(Role.VOLUNTEER).build();
            teamRepository.save(member);
            return "Accept Request Completed";
        } else if (partyJoinDto.status().get() == PartyJoinStatus.REFUSE) {
            log.info("=== REFUSE ===");
            return "Refuse Request Completed";
        }
        return null;
    }

    public List<ResponsePartyDto> getPartyList(MainPageDto mainPageDto, Pageable pageable) {
        List<Party> partyList;

        CalculateDto calculateDto = calculate(mainPageDto.getLongitude(), mainPageDto.getLatitude());
        partyList = partyRepositoryImpl.getPartyList(calculateDto.getMinLatitude(), calculateDto.getMaxLatitude(), calculateDto.getMinLongitude(), calculateDto.getMaxLongitude(), pageable);

        List<ResponsePartyDto> responsePartyList = partyList.stream()
                .map(ResponsePartyDto::toDto)
                .collect(Collectors.toList());

        return responsePartyList;
    }

    // 유저의 위도, 경도를 바탕으로 반경 10km 위도, 경도값 계산
    private CalculateDto calculate(double userLongitude, double userLatitude) {
        double earthRadius = 6371; // 지구 반지름 (단위: km)

        double minLat = userLatitude - (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        double maxLat = userLatitude + (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        double minLon = userLongitude - (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        double maxLon = userLongitude + (MAX_DISTANCE / earthRadius) * (180.0 / Math.PI);
        CalculateDto calculateDto = new CalculateDto(minLat, maxLat, minLon, maxLon);

        return calculateDto;
    }
}
