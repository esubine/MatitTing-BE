package com.kr.matitting.controller;

import com.kr.matitting.dto.ResponseChatRoomInfoDto;
import com.kr.matitting.dto.ResponseChatRoomListDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    @Operation(summary = "내 채팅방 가져오기", description = "내 채팅방 조회 API \n\n" +
                                                        "[로직 설명]  \n\n" +
                                                        "유저가 참여하고 있는 채팅방들을 조회하여 response로 리턴합니다. \n\n" +
                                                        "※ 유저 정보는 헤더에 있는 토큰으로 식별합니다. \n\n" +
                                                        "※ size default는 10입니다. 필수로 입력하지 않아도 됩니다. \n\n" +
                                                        "(기본 Size가 10으로 정해져있습니다. 다른 size로 조회하고자 하는 경우 적절하게 입력해주세요) \n\n" +
                                                        "※ 정렬 기준은 최신순입니다. \n\n" +
                                                        "※ page default는 0입니다. 필수로 입력하지 않아도 됩니다. \n\n" +
                                                        "(다음 페이지 조회를 원하는 경우 ResponsePageInfoDto의 page에 +1하여 요청하면 다음 페이지 조회가 가능합니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseChatRoomListDto.class)))})
    })
    @GetMapping
    public ResponseEntity<ResponseChatRoomListDto> getAllRooms(@AuthenticationPrincipal User user,
                                                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
                                                               @RequestParam(value = "page", defaultValue = "0", required = false) Integer page
                                                                ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(chatService.getChatRooms(user.getId(), pageable));
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

        return ResponseEntity.ok(chatService.getChatRoomInfo(chatRoomId, user.getId()));
    }

    @Operation(summary = "내 채팅방 검색", description = "내 채팅방 검색 API \n\n" +
                                                    "[로직 설명]  \n\n" +
                                                    "searchTitle을 파라미터로 검색어를 입력받습니다. \n\n" +
                                                    "유저가 참여하고 있는 채팅방들 중 채팅방의 이름이 searchTitle을 포함하고 있는 조회하여 response로 리턴합니다. \n\n" +
                                                    "※ 유저 정보는 헤더에 있는 토큰으로 식별합니다. \n\n" +
                                                    "※ size default는 10입니다. 필수로 입력하지 않아도 됩니다. \n\n" +
                                                    "(기본 Size가 10으로 정해져있습니다. 다른 size로 조회하고자 하는 경우 적절하게 입력해주세요) \n\n" +
                                                    "※ 정렬 기준은 최신순입니다. \n\n" +
                                                    "※ page default는 0입니다. 필수로 입력하지 않아도 됩니다. \n\n" +
                                                    "(다음 페이지 조회를 원하는 경우 ResponsePageInfoDto의 page에 +1하여 요청하면 다음 페이지 조회가 가능합니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseChatRoomListDto.class)))})
    })
    @GetMapping("/search")
    public ResponseEntity<ResponseChatRoomListDto> getChatRoomsByTitleSearch(@AuthenticationPrincipal User user,
                                                                           @RequestParam(value ="title") String searchTitle,
                                                                           @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
                                                                           @RequestParam(value = "page", defaultValue = "0", required = false) Integer page){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(chatService.getChatRoomsByTitleSearch(user.getId(), pageable, searchTitle));
    }

}