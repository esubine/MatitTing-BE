package com.kr.matitting.service;

import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyJoinStatus;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.CreatePartyRequest;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.Team;
import org.webjars.NotFoundException;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        if (partyJoinDto.getPartyId() == null ||
                partyJoinDto.getParentId() == null ||
                partyJoinDto.getUserId() == null) {
            log.error("JoinParty:[Request Data is null!!]");
            throw new IllegalStateException("데이터의 요청이 잘못되었습니다.");
        }

        Party party = partyRepository.findById(partyJoinDto.getPartyId()).orElseThrow(NotFoundException::new);
        PartyJoin partyJoin = PartyJoin.builder().party(party).parentId(partyJoinDto.getParentId()).userId(partyJoinDto.getUserId()).build();
        partyJoinRepository.save(partyJoin);
    }

    public String decideUser(PartyJoinDto partyJoinDto) throws NotFoundException {
        if (partyJoinDto.getStatus() == PartyJoinStatus.WAIT) {
            log.info("Party Join Status 정보가 잘못 Request 되었습니다.");
            throw new IllegalStateException("NOT FOUND Accept or Refuse status");
        }

        PartyJoin findPartyJoin = partyJoinRepository.findByPartyIdAndParentIdAndUserId(
                partyJoinDto.getPartyId(),
                partyJoinDto.getParentId(),
                partyJoinDto.getUserId()).orElseThrow(NotFoundException::new);
        partyJoinRepository.delete(findPartyJoin);

        if (partyJoinDto.getStatus() == PartyJoinStatus.ACCEPT) {
            //파티방 Table에 정보를 입력
            User user = userRepository.findById(partyJoinDto.getUserId()).orElseThrow(NotFoundException::new);
            Party party = partyRepository.findById(partyJoinDto.getPartyId()).orElseThrow(NotFoundException::new);
            Team member = Team.builder().user(user).party(party).role(Role.VOLUNTEER).build();
            teamRepository.save(member);
            return "Accept Request Completed";
        } else if (partyJoinDto.getStatus() == PartyJoinStatus.REFUSE) {
            return "Refuse Request Completed";
        }
        return null;
    }

    public List<PartyJoin> getJoinList(PartyJoinDto partyJoinDto) {
        if (partyJoinDto.getPartyId() == null ||
                partyJoinDto.getParentId() == null) {
            log.error("GetJoinList:[Request Data is null!!]");
            throw new IllegalStateException("데이터의 요청이 잘못되었습니다.");
        }
        return partyJoinRepository.findByPartyIdAndParentId(partyJoinDto.getPartyId(), partyJoinDto.getParentId());
    }
}
