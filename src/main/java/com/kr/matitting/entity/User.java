package com.kr.matitting.entity;

import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;    //회원 Id

    @Column(length = 30, unique = true, name = "social_id")
    private String socialId;   //카카오 or 네이버 인증 Id

    @Column(nullable = false, length = 30, unique = true)
    private String email;   //이메일

    //닉네임
    @Column(nullable = false, length = 30, unique = true)
    private String nickname; //닉네임

    //나이
    @Column(nullable = false, length = 30)
    private int age; //나이

    //프로필 이미지
    @Column(nullable = true, name = "user_img")
    private String imgUrl; //이미지 url

    @Column
    private String city; // 사는 도시

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE
}
