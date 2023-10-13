package com.kr.matitting.service;

import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyJoinStatus;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.CreatePartyRequest;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.entity.*;
import com.kr.matitting.exception.menu.MenuException;
import com.kr.matitting.exception.menu.MenuExceptionType;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.partyjoin.PartyJoinException;
import com.kr.matitting.exception.partyjoin.PartyJoinExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {
    private final PartyJoinRepository partyJoinRepository;
    private final PartyTeamRepository teamRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;

    private final MenuRepository menuRepository;
    private final MapService mapService;

    public void createParty(CreatePartyRequest request) {
        log.info("=== createParty() start ===");
        Long userId = 1L;
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        // 위도, 경도 -> 주소 변환
        String address = mapService.coordToAddr(request.getLongitude(), request.getLatitude());

        //모집 기간(선택 사항)을 따로 지정 안 하면 식사 시간(필수 입력 사항) 1시간 전으로
        LocalDateTime deadline = request.getDeadline();
        if (deadline == null) {
            deadline = request.getPartyTime().minusHours(1L);
        }

        //썸네일 없는 경우 카테고리에 따라 이미지 설정
        String thumbnail = request.getThumbnail();
        if (thumbnail == null) {
            PartyCategory category = request.getCategory();
            switch (category) {
                case KOREAN -> thumbnail = "한식.img";
                case WESTERN -> thumbnail = "양식.img";
                case CHINESE -> thumbnail = "중식.img";
                case JAPANESE -> thumbnail = "일식.img";
                case ETC -> thumbnail = "기타.img";
            }
        }

        Menu menu = createBaseMenuBuilder(request)
                .thumbnail(thumbnail)
                .build();

        menuRepository.save(menu);

        // address 변환, deadline, thumbnail이 null일 경우 처리하는 로직 처리 후 생성
        Party party = createBasePartyBuilder(request, user)
                .address(address)
                .deadline(deadline)
                .menu(menu)
                .build();

        partyRepository.save(party);
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
            Menu menu = menuRepository.findByMenu(partyUpdateDto.menu().get()).orElseThrow(() -> new MenuException(MenuExceptionType.NOT_FOUND_MENU));
            party.setMenu(menu);
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

    // address, deadline, thumbnail와 같이 변환이나 null인 경우 처리가 필요한 필드는 제외하고 나머지 필드는 빌더패턴으로 생성
    private Party.PartyBuilder createBasePartyBuilder(CreatePartyRequest request, User user) {
        return Party.builder()
                .partyTitle(request.getTitle())
                .partyContent(request.getContent())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .partyTime(request.getPartyTime())
                .totalParticipant(request.getTotalParticipant())
                .gender(request.getGender())
                .age(request.getAge())
                .status(PartyStatus.RECRUIT)
                .user(user);
    }

    private Menu.MenuBuilder createBaseMenuBuilder(CreatePartyRequest request) {
        return Menu.builder()
                .menu(request.getMenu())
                .category(request.getCategory());
    }

    public void joinParty(PartyJoinDto partyJoinDto){
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

    public List<PartyJoin> getJoinList(PartyJoinDto partyJoinDto) {
        if (partyJoinDto.partyId() == null ||
                partyJoinDto.leaderId() == null) {
            log.error("GetJoinList:[Request Data is null!!]");
            throw new PartyJoinException(PartyJoinExceptionType.NULL_POINT_PARTY_JOIN);
        }
        return partyJoinRepository.findByPartyIdAndLeaderId(partyJoinDto.partyId(), partyJoinDto.leaderId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
    }
}
