package com.kr.matitting.controller;

import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.s3.S3Uploader;
import com.kr.matitting.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/party")
public class PartyController {
    private final PartyService partyService;
    private final S3Uploader s3Uploader;

    // 파티 모집 글 생성
    @PostMapping("/")
    public ResponseEntity<Map<String, Long>> createParty(
            @RequestBody @Valid PartyCreateDto request
    ) {
        Map<String, Long> partyId = partyService.createParty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(partyId);
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestPart(value = "image") MultipartFile multipartFile
    ) throws IOException {
        Map<String, String> imgUrl = s3Uploader.upload(multipartFile);
        return ResponseEntity.ok().body(imgUrl);
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

    @DeleteMapping("/{partyId}")
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

    @GetMapping("/main-page")
    public ResponseEntity<List<ResponsePartyDto>> getPartyList(
            @RequestBody MainPageDto mainPageDto,
            Pageable pageable
    ) {
        List<ResponsePartyDto> partyList = partyService.getPartyList(mainPageDto, pageable);
        return ResponseEntity.ok().body(partyList);
    }

}
