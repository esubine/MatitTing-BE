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

import java.time.LocalDateTime;
import java.util.List;

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
    @Schema(description = "연령대", nullable = false, example = "TWENTY")
    @NotNull
    private PartyAge partyAge;

    @Schema(description = "사용자 성별", nullable = false, example = "MALE")
    @NotNull
    private Gender userGender;

    @Schema(description = "사용자 나이", nullable = false, example = "26")
    @NotNull
    private Integer userAge;
    @Schema(description = "신청 일자", example = "2024-03-29T10:15:30.123456789")
    private LocalDateTime createAt;
    @Schema(description = "한줄 소개", example = "안녕하세요")
    private String oneLineIntroduce;
    @Schema(description = "조건 일치 여부", example = "true")
    private Boolean typeMatch;

    public static InvitationRequestDto toDto(PartyJoin partyJoin, User user, Role role) {
        Party party = partyJoin.getParty();

        List<Gender> genderType = party.getGender().equals(Gender.ALL) ? List.of(Gender.MALE, Gender.FEMALE) : List.of(party.getGender());
        boolean genderMatch = genderType.contains(user.getGender());
        boolean ageMatch = false;
        switch (party.getAge()) {
            case ALL :
                ageMatch = true;
                break;
            case TWENTY:
                ageMatch = 20 <= user.getAge() && user.getAge() < 30;
                break;
            case THIRTY:
                ageMatch = 30 <= user.getAge() && user.getAge() < 40;
                break;
            case FORTY:
                ageMatch = 40 <= user.getAge() && user.getAge() < 50;
                break;
        }

        return InvitationRequestDto.builder()
                .partyId(party.getId())
                .partyTitle(party.getPartyTitle())
                .nickname(role.equals(Role.VOLUNTEER) ? null:user.getNickname())
                .imgUrl(user.getImgUrl())
                .partyGender(party.getGender())
                .partyAge(party.getAge())
                .userGender(user.getGender())
                .userAge(user.getAge())
                .createAt(partyJoin.getCreateDate())
                .oneLineIntroduce(partyJoin.getOneLineIntroduce())
                .typeMatch(genderMatch&&ageMatch)
                .build();
    }
}
