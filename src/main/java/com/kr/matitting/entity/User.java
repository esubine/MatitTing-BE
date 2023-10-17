package com.kr.matitting.entity;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
@Entity
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;    //회원 Id

    @Column(nullable = false, length = 30, unique = true)
    private String socialId;   //카카오 or 네이버 인증 Id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    @Column(nullable = false, length = 30, unique = true)
    private String email;   //이메일

    @Column(nullable = false, length = 30, unique = true)
    private String nickname; //닉네임

    @Column(nullable = false, length = 30)
    private Integer age; //나이

    @Column(name = "user_img")
    private String imgUrl; //프로필 이미지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; //성별

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; //신규유저 or 기존유저

}
