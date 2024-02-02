package com.kr.matitting.entity;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
@Entity
public class User extends BaseTimeEntity{
    @Schema(description = "사용자 id", nullable = false, example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;    //회원 Id

    @Schema(description = "소셜 id", nullable = false, example = "3311311")
    @Column(nullable = false, length = 50, unique = true)
    private String socialId;   //카카오 or 네이버 인증 Id

    @Schema(description = "소셜 Type", nullable = false, example = "KAKAO")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    @Schema(description = "사용자 email", nullable = false, example = "parksn5029@naver.com")
    @Column(nullable = false, length = 30, unique = true)
    private String email;   //이메일

    @Schema(description = "사용자 닉네임", nullable = false, example = "안경잡이 개발자")
    @Column(nullable = false, length = 30, unique = true)
    private String nickname; //닉네임

    @Schema(description = "사용자 나이", nullable = false, example = "26")
    @Column(nullable = false, length = 30)
    private Integer age; //나이

    @Schema(description = "사용자 프로필 이미지", nullable = true, example = "증명사진.jpg")
    @Column(name = "user_img")
    private String imgUrl; //프로필 이미지

    @Schema(description = "사용자 성별", nullable = false, example = "MALE")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; //성별

    @Schema(description = "사용자 role", nullable = false, example = "USER")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; //신규유저 or 기존유저

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Party> partyList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> myChatRoom;

}
