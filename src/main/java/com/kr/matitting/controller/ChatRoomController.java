package com.kr.matitting.controller;

import com.kr.matitting.dto.*;
import com.kr.matitting.entity.User;
import com.kr.matitting.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    @Operation(summary = "내 채팅방 가져오기", description = "내 채팅방 조회 API \n\n" +
                                                        "[로직 설명]  \n\n" +
                                                        "유저가 참여하고 있는 채팅방들을 조회하여 response로 리턴합니다. \n\n" +
                                                        "※ 유저 정보는 헤더에 있는 토큰으로 식별합니다. \n\n" +
                                                        "※ size default는 5입니다. \n\n" +
                                                        "※ 정렬 기준은 최신순입니다. \n\n" +
                                                        "※ lastChatRoomId는 이전에 어떤 채팅방까지 리턴하였는지 식별하기 위한 값입니다. 처음 요청하는 경우에는 0으로 입력하여 주시길 바랍니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseChatRoomListDto.class)))})
    })
    @GetMapping
    public ResponseEntity<ResponseChatRoomListDto> getAllRooms(@AuthenticationPrincipal User user,
                                                               @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
                                                               @RequestParam(value = "lastChatRoomId") Long lastChatRoomId) {
        Pageable pageable = PageRequest.of(0, size ,Sort.Direction.DESC, "modifiedDate" );
        return ResponseEntity.ok(chatService.getChatRooms(user.getId(), lastChatRoomId, pageable));
    }


    @Operation(summary = "채팅방 내 유저들 조회", description = "채팅방 참여 중인 유저 조회 API \n\n" +
                                                        "[로직 설명] \n\n" +
                                                        "입력한 room id에 해당하는 채팅방에 참여중인 유저 정보를 리턴합니다. \n\n" +
                                                        "※ 헤더의 토큰으로 식별한 유저(요청자)가 해당 채팅방에 참여하고 있지 않은 경우 \"회원 정보가 없습니다.\"으로 리턴됩니다. \n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseChatRoomUserDto.class)))})
    })
    @GetMapping("/user/{roomId}")
    public ResponseEntity<List<ResponseChatRoomUserDto>> getUserInfos(@PathVariable Long roomId,
                                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getRoomUsers(roomId, user.getId()));
    }

    @Operation(summary = "채팅방의 정보조회", description = "채팅방의 정보조회 API \n\n" +
                                                        "[로직 설명] \n\n" +
                                                        "입력한 chat room id에 해당하는 채팅방의 정보를 리턴합니다. \n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseChatRoomInfoDto.class)))})
    })
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ResponseChatRoomInfoDto> getChatRoomInfo(@PathVariable Long chatRoomId,
                                                                    @AuthenticationPrincipal User user){

        return ResponseEntity.ok(chatService.getChatRoomInfo(chatRoomId));
    }

}