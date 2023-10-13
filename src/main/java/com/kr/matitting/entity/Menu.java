package com.kr.matitting.entity;

import com.kr.matitting.constant.PartyCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "menu")
public class Menu extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(nullable = false, name = "menu")
    private String menu; // 메뉴
    @Column(nullable = false, name = "category")
    @Enumerated(EnumType.STRING)
    private PartyCategory category; // 음식 카테고리

    @Column(nullable = false, name = "thumbnail")
    private String thumbnail; //파티 썸네일

}
