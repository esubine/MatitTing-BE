package com.kr.matitting.controller;

import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.kr.matitting.s3.S3Uploader;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/party")
public class PartyController {
    private final PartyService partyService;
    private final S3Uploader s3Uploader;

    // 파티 모집 글 생성
    @PostMapping("/")
    public ResponseEntity<String> createParty(
            @RequestBody @Valid PartyCreateDto request
    ) {
        partyService.createParty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("파티 글이 생성되었습니다.");
    }

    @PostMapping("/image")
    public String uploadImage(
            @RequestPart(value = "image") MultipartFile multipartFile
    ) throws IOException {
        return s3Uploader.upload(multipartFile);
    }

    @PatchMapping("/")
    public void updateParty(PartyUpdateDto partyUpdateDto) {
        partyService.partyUpdate(partyUpdateDto);
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<ResponsePartyDto> partyDetail(@PathVariable Long partyId) {
        ResponsePartyDto partyInfo = partyService.getPartyInfo(partyId);
        return ResponseEntity.ok().body(partyInfo);
    }

    @GetMapping("/{partyId}")
    public void partyDelete(@PathVariable Long partyId) {
        partyService.deleteParty(partyId);
    }

    //유저가 파티방에 참가를 요청하는 logic
    @PostMapping("/participation")
    public ResponseEntity<String> JoinParty(PartyJoinDto partyJoinDto) throws Exception {

        partyService.joinParty(partyJoinDto);
        return ResponseEntity.ok().body("Success join request!");
    }

    //방장이 파티방에 대한 수락/거절을 하는 logic
    @PostMapping("/decision")
    public ResponseEntity<String> AcceptRefuseParty(PartyJoinDto partyJoinDto) throws Exception {
        String result = partyService.decideUser(partyJoinDto);
        return ResponseEntity.ok().body(result);
    }
}
