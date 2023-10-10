package com.kr.matitting.service;

import com.kr.matitting.dto.CreatePartyRequest;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;

    private final MapService mapService;
//    private final PartyRepositoryCustom partyRepositoryCustom;
//
//    public Page<Party> getPartyPage(PartySearchCondDto partySearchCondDto, Pageable pageable) {
//        return partyRepositoryCustom.searchPage(partySearchCondDto, pageable);
//    }

    public void createParty(CreatePartyRequest request) {
        Long userId = 1L;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user 정보가 없습니다."));

        String address = mapService.coordToAddr(request.getLongitude(), request.getLatitude());

        Party party = Party.create(request, user, address);

        partyRepository.save(party);
    }
}
