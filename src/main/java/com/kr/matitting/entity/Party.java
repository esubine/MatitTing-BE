package com.kr.matitting.entity;

import com.kr.matitting.constant.PartyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Party extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partyId;
    private String partyTitle;
    private String menu;
    @Enumerated(EnumType.STRING)
    private PartyStatus status;

    private LocalDateTime partyDeadline;

    private int hit = 0;
}
