package com.kr.matitting.entity;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.dto.CreatePartyRequest;
import jakarta.persistence.*;
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
    @Column(name = "party_title")
    private String partyTitle; // 파티 모집 제목
    @Column(name = "party_content", length = 500)
    private String partyContent; // 파티 모집 글
    @Column(name = "menu")
    private String menu; // 메뉴
    @Column(name = "address")
    private String address; // 주소
    @Enumerated(EnumType.STRING)
    private PartyStatus status; // 파티 상태
    @Column(name = "party_deadline")
    private LocalDateTime partyDeadline; // 파티 모집 시간
    @Column(name = "party_time")
    private LocalDateTime partyTime; // 파티 시작 시간
    @Column(name = "total_participant")
    private int totalParticipant; // 모집 인원
    @Column(name = "participant_count")
    @ColumnDefault("0")
    private int participantCount; // 참가자 수
    @Column(name = "hit")
    @ColumnDefault("0")
    private int hit;
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Party(String partyTitle, String partyContent, LocalDateTime partyTime, LocalDateTime partyDeadline,
                 PartyStatus partyStatus, String address, String menu, User user, int totalParticipant, int participantCount) {
        this.partyTitle = partyTitle;
        this.partyContent = partyContent;
        this.user = user;
        this.partyTime = partyTime;
        this.partyDeadline = partyDeadline;
        this.status = PartyStatus.ON;
        this.address = address;
        this.totalParticipant = totalParticipant;
        this.participantCount = 0;
        this.menu = "메뉴";
    }

    public static Party create(CreatePartyRequest request, User user, String address) {
        return Party.builder()
                .partyTitle(request.getPartyTitle())
                .partyContent(request.getPartyContent())
                .partyTime(request.getPartyTime())
                .partyDeadline(request.getPartyDeadline())
                .totalParticipant(request.getTotalParticipant())
                .address(address)
                .user(user)
                .build();
    }
}
