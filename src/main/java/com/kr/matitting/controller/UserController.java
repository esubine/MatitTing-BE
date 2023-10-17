package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/api/profile")
    public void myProfileUpdate(UserUpdateDto userUpdateDto) {
        userService.update(userUpdateDto);
    }

    @GetMapping("/api/partyStatus")
    public ResponseEntity<List<PartyCreateDto>> myPartyList(@RequestParam Long userId, @RequestParam Role role) {
        List<PartyCreateDto> myPartyList = userService.getMyPartyList(userId, role);
        return ResponseEntity.ok().body(myPartyList);
    }
}
