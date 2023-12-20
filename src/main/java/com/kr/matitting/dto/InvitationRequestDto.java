package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.entity.PartyJoin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequestDto {
    @Schema(description = "파티 id", nullable = false, example = "1")
    @NotNull
    private Long partyId;
    @Schema(description = "파티 제목", nullable = false, example = "붕어빵 드실 분")
    @NotNull
    private String partyTitle;
    @Schema(description = "사용자 닉네임", nullable = false, example = "안경잡이 개발자")
    private String nickname; //닉네임
    @Schema(description = "사용자 프로필 이미지", nullable = true, example = "증명사진.jpg")
    @Column(name = "user_img")
    private String imgUrl;
    @Schema(description = "성별", nullable = false, example = "ALL")
    @NotNull
    private Gender partyGender;
    @Schema(description = "연령대", nullable = false, example = "2030")
    @NotNull
    private PartyAge partyAge;

    @Schema(description = "사용자 성별", nullable = false, example = "MALE")
    @NotNull
    private Gender userGender;

    @Schema(description = "사용자 나이", nullable = false, example = "26")
    @NotNull
    private Integer userAge;

    public static InvitationRequestDto toDto(PartyJoin partyJoin, User user, Role role) {
        Party party = partyJoin.getParty();

        return InvitationRequestDto.builder()
                .partyId(party.getId())
                .partyTitle(party.getPartyTitle())
                .nickname(role.equals(Role.VOLUNTEER) ? null:user.getNickname())
                .imgUrl(user.getImgUrl())
                .partyGender(party.getGender())
                .partyAge(party.getAge())
                .userGender(user.getGender())
                .userAge(user.getAge())
                .build();
    }
}