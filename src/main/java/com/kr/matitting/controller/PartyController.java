package com.kr.matitting.controller;

import com.kr.matitting.dto.CreatePartyRequest;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.kr.matitting.dto.PartyJoinDto;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;

    // 파티 모집 글 생성
    @PostMapping("/api/party")
    public ResponseEntity<String> createParty(
            @RequestBody @Valid CreatePartyRequest request
    ) {
        partyService.createParty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("파티 글이 생성되었습니다.");
    }

    @PatchMapping("/api/party/update")
    public void updateParty(PartyUpdateDto partyUpdateDto) {
        partyService.partyUpdate(partyUpdateDto);
    }

    //유저가 파티방에 참가를 요청하는 logic
    @PostMapping("/api/party/participation")
    public ResponseEntity<String> JoinParty(PartyJoinDto partyJoinDto) throws Exception {
        partyService.joinParty(partyJoinDto);
        return ResponseEntity.ok().body("Success join request!");
    }

    //방장이 파티방에 대한 수락/거절을 하는 logic
    @PostMapping("/api/party/decision")
    public ResponseEntity<String> AcceptRefuseParty(PartyJoinDto partyJoinDto) throws Exception {
        String result = partyService.decideUser(partyJoinDto);
        return ResponseEntity.ok().body(result);
    }
}
