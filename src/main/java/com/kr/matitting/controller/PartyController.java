package com.kr.matitting.controller;

import com.kr.matitting.dto.CreatePartyRequest;
import com.kr.matitting.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;
    @PostMapping("/party")
    public void createParty(
            @RequestBody CreatePartyRequest request
    ) {
        partyService.createParty(request);
    }


}
