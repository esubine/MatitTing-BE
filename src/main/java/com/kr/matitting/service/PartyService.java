package com.kr.matitting.service;

import com.kr.matitting.dto.CreatePartyRequest;
import org.webjars.NotFoundException;
import com.kr.matitting.constant.PartyJoinStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.data.crossstore.ChangeSetPersister.*;

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
    private final MapService mapService;

    // TODO
    //  : 예외처리
    //  : return 값
    public void createParty(CreatePartyRequest request) {
        Long userId = 1L;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user 정보가 없습니다."));

        if(request.getPartyTitle() == null || request.getPartyContent() == null
                || request.getPartyTime() == null ){
            log.error("CreatePartyRequest:[Request Data is null!!]");
            throw new IllegalStateException("잘못된 요청 값 입니다.");
        }

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

            switch (category){
                case KOREAN -> thumbnail = "한식.img";
                case WESTERN -> thumbnail = "양식.img";
                case CHINESE -> thumbnail = "중식.img";
                case JAPANESE -> thumbnail = "일식.img";
                case ETC -> thumbnail = "기타.img";
            }
        }

        Party party = createBasePartyBuilder(request, user)
                .address(address)
                .deadline(deadline)
                .thumbnail(thumbnail)
                .build();

        partyRepository.save(party);
    }

    /**
     * 추가로 필요한 필드
     * address, deadline, thumbnail
     */
    private Party.PartyBuilder createBasePartyBuilder(CreatePartyRequest request, User user) {
        return Party.builder()
                .title(request.getPartyTitle())
                .content(request.getPartyContent())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .menu(request.getMenu())
                .partyTime(request.getPartyTime())
                .totalParticipant(request.getTotalParticipant())
                .category(request.getCategory())
                .gender(request.getGender())
                .status(PartyStatus.ON)
                .user(user);
    }

    public void joinParty(PartyJoinDto partyJoinDto) throws NotFoundException {
        log.info("=== joinParty() start ===");

        if (partyJoinDto.getPartyId() == null ||
                partyJoinDto.getLeaderId() == null ||
                partyJoinDto.getUserId() == null) {
            log.error("=== JoinParty:Request Data is null ===");
            throw new PartyJoinException(PartyJoinExceptionType.NULL_POINT_PARTY_JOIN);
        }

        Party party = partyRepository.findById(partyJoinDto.getPartyId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
        PartyJoin partyJoin = PartyJoin.builder().party(party).leaderId(partyJoinDto.getLeaderId()).userId(partyJoinDto.getUserId()).build();
        partyJoinRepository.save(partyJoin);
    }

    public String decideUser(PartyJoinDto partyJoinDto){
        log.info("=== decideUser() start ===");

        if (partyJoinDto.getStatus() == PartyJoinStatus.ACCEPT || partyJoinDto.getStatus() == PartyJoinStatus.REFUSE) {
            log.error("=== Party Join Status was requested incorrectly ===");
            throw new PartyJoinException(PartyJoinExceptionType.WRONG_STATUS);
        }

        PartyJoin findPartyJoin = partyJoinRepository.findByPartyIdAndParentIdAndUserId(
                partyJoinDto.getPartyId(),
                partyJoinDto.getLeaderId(),
                partyJoinDto.getUserId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
        partyJoinRepository.delete(findPartyJoin);

        if (partyJoinDto.getStatus() == PartyJoinStatus.ACCEPT) {
            log.info("=== ACCEPT ===");
            User user = userRepository.findById(partyJoinDto.getUserId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
            Party party = partyRepository.findById(partyJoinDto.getPartyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
            Team member = Team.builder().user(user).party(party).role(Role.VOLUNTEER).build();
            teamRepository.save(member);
            return "Accept Request Completed";
        } else if (partyJoinDto.getStatus() == PartyJoinStatus.REFUSE) {
            log.info("=== REFUSE ===");
            return "Refuse Request Completed";
        }
        return null;
    }

    public List<PartyJoin> getJoinList(PartyJoinDto partyJoinDto) {
        if (partyJoinDto.getPartyId() == null ||
                partyJoinDto.getLeaderId() == null) {
            log.error("GetJoinList:[Request Data is null!!]");
            throw new PartyJoinException(PartyJoinExceptionType.NULL_POINT_PARTY_JOIN);
        }
        return partyJoinRepository.findByPartyIdAndParentId(partyJoinDto.getPartyId(), partyJoinDto.getLeaderId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));
    }
}
