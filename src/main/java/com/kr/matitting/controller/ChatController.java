package com.kr.matitting.controller;

import com.kr.matitting.dto.ChatEvictDto;
import com.kr.matitting.dto.ChatMessageDto;
import com.kr.matitting.dto.ResponseChatListDto;
import com.kr.matitting.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "채팅 기록 가져오기", description = "채팅 기록 조회 API \n\n" +
                                                        "[로직 설명] \n\n" +
                                                        "채팅방 id에 해당하는 채팅방의 채팅 기록을 조회하여 리턴합니다. \n\n" +
                                                        "※ size default는 10입니다. 필수로 입력하지 않아도 됩니다. \n\n" +
                                                            "(기본 Size가 10으로 정해져있습니다. 다른 size로 조회하고자 하는 경우 적절하게 입력해주세요) \n\n" +
                                                        "※ 정렬 기준은 최신순입니다. \n\n" +
                                                        "※ page default는 0입니다. 필수로 입력하지 않아도 됩니다. \n\n" +
                                                        "(다음 페이지 조회를 원하는 경우 ResponsePageInfoDto의 page에 +1하여 요청하면 다음 채팅 조회가 가능합니다.)")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseChatListDto.class)))})
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<ResponseChatListDto> getChats(@PathVariable Long roomId,
                                                        @AuthenticationPrincipal Long userId,
                                                        @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
                                                        @RequestParam(value = "page", defaultValue = "0", required = false) Integer page
                                                        ) {
        PageRequest pageable = PageRequest.of(page, size);
        ResponseChatListDto responseChatListDto = chatService.getChats(userId, roomId, pageable);
        return ResponseEntity.ok(responseChatListDto);
    }

    //채팅 메시지 전송
    @Operation(summary = "채팅 메시지 전송", description = "메시지 전송 API")
    @MessageMapping("/message")
    public void message(ChatMessageDto chatMessageDto) {
        chatService.sendMessage(chatMessageDto);
    }

    //유저 강제 퇴장
    @Operation(summary = "유저 강퇴 API", description = "유저 강퇴 API \n\n" +
            "[로직 설명] \n\n" +
            "1. 강퇴할 유저의 id를 request합니다. \n\n" +
            "2. 요청자가 HOST인 경우에만 유저를 강퇴할 수 있습니다.")
    @DeleteMapping("/{roomId}")
    public void evictUser(@PathVariable Long roomId,
                          @RequestBody ChatEvictDto chatEvictDto,
                          @AuthenticationPrincipal Long userId) {
        chatService.evictUser(userId, chatEvictDto.getTargetChatUserId(), roomId);
    }
}
