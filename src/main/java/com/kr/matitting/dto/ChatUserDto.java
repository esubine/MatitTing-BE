package com.kr.matitting.dto;

import com.kr.matitting.constant.ChatUserRole;
import com.kr.matitting.entity.ChatUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public interface ChatUserDto {
    @Getter
    class ChatUserResponse {
        private Long chatUserId;
        private ChatUserRole role;
        private String nickname;

        public ChatUserResponse(ChatUser chatUser) {
            this.chatUserId = chatUser.getId();
            this.role = chatUser.getUserRole();
            this.nickname = chatUser.getNickname();
        }
    }

    @Getter
    class ChatUserInfoResponse {
        private ChatUserResponse myInfo;
        private List<ChatUserResponse> users;

        public ChatUserInfoResponse(ChatUser myInfo, List<ChatUser> participants) {
            this.myInfo = new ChatUserResponse(myInfo);
            this.users = participants.stream()
                    .map(ChatUserResponse::new)
                    .toList();
        }
    }

    @Getter
    @NoArgsConstructor
    class ChatEvict {
        private Long targetId;
    }
}
