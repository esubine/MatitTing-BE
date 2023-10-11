package com.kr.matitting.service;

import com.kr.matitting.dto.CreatePartyRequest;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.partyjoin.PartyJoinException;
import com.kr.matitting.exception.partyjoin.PartyJoinExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public void createParty(CreatePartyRequest request) {
        Long userId = 1L;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user 정보가 없습니다."));

        String address = mapService.coordToAddr(request.getLongitude(), request.getLatitude());

        Party party = Party.create(request, user, address);

        partyRepository.save(party);
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
