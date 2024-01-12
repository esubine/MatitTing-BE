package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseUserDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "소셜 id", nullable = false, example = "3311311")
    private String socialId;   //카카오 or 네이버 인증 Id

    @Schema(description = "소셜 Type", nullable = false, example = "KAKAO")
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    @Schema(description = "사용자 email", nullable = false, example = "parksn5029@naver.com")
    private String email;   //이메일

    @Schema(description = "사용자 닉네임", nullable = false, example = "안경잡이 개발자")
    private String nickname; //닉네임

    @Schema(description = "사용자 나이", nullable = false, example = "26")
    private Integer age; //나이

    @Schema(description = "사용자 프로필 이미지", nullable = true, example = "증명사진.jpg")
    private String imgUrl; //프로필 이미지

    @Schema(description = "사용자 성별", nullable = false, example = "MALE")
    private Gender gender; //성별

    @Schema(description = "사용자 role", nullable = false, example = "USER")
    private Role role; //신규유저 or 기존유저
}
