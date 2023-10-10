package com.kr.matitting.entity;

import com.kr.matitting.constant.PartyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PARTY")
public class Party extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Long id;
    private String title;
    private String menu;
    @Enumerated(EnumType.STRING)
    private PartyStatus status;

    private LocalDateTime deadline;
    @ColumnDefault("0")
    private int hit;
}
