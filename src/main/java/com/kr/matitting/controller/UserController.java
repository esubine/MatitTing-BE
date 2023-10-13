package com.kr.matitting.controller;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/api/profile/update")
    public void myProfileUpdate(UserUpdateDto userUpdateDto) {
        userService.update(userUpdateDto);
    }

    @GetMapping("/api/partyStatus")
    public ResponseEntity<List<Party>> myPartyList(@RequestParam Long userId, @RequestParam PartyStatus status) {
        List<Party> myPartyList = userService.getMyPartyList(userId, status);
        return ResponseEntity.ok().body(myPartyList);
    }
}
