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
    
    public void createParty(CreatePartyRequest request) {
        Long userId = 1L;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user 정보가 없습니다."));

        String address = mapService.coordToAddr(request.getLongitude(), request.getLatitude());

        Party party = Party.create(request, user, address);

        partyRepository.save(party);
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
