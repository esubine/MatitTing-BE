package com.kr.matitting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.PriorityQueue;

@Getter
@Entity
@Builder
@AllArgsConstructor
public class Member extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;    //회원 Id

    @Column(length = 30, unique = true)
    private Long oauthId;   //카카오 or 네이버 인증 Id

    @Column(nullable = false, length = 30, unique = true)
    private String email;   //이메일

    //닉네임
    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    //나이
    @Column(nullable = false, length = 30)
    private int age;

    //프로필 이미지
    @Column(nullable = true, name = "member_img")
    private String imgUrl;
}
