package com.kr.matitting.entity;

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
@Table(name = "PARTY_JOIN")
public class PartyJoin extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;
    @Column(name = "leader_id")
    private Long leaderId;
    @Column(name = "user_id")
    private Long userId;
}
