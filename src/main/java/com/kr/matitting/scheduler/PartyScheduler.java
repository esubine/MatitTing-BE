package com.kr.matitting.scheduler;

import com.kr.matitting.constant.NotificationType;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.Team;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PartyScheduler {

    private final PartyRepository partyRepository;
    private final NotificationService notificationService;

//    @Scheduled(cron = "0 0 */1 * * ?", zone = "Asia/Seoul")
    @Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
    public void checkEndParty() {

        log.info("=== party 상태 변경 스케줄러 시작 ===");

//        LocalDateTime overedPartyTime = LocalDateTime.now().minusHours(5);
        LocalDateTime overedPartyTime = LocalDateTime.now().minusMinutes(1);

        List<Party> partyList = partyRepository.getPartyIsNotPartyFinish(PartyStatus.PARTY_FINISH);

        int changeCount = 0;

        for (Party party : partyList) {
            if (party.getPartyTime().isBefore(overedPartyTime) && party.getStatus() != PartyStatus.PARTY_FINISH) {
                party.setStatus(PartyStatus.PARTY_FINISH);
                changeCount++;
                partyRepository.save(party);

                //후기 알림
                List<Team> teamList = party.getTeamList();
                teamList.stream()
                        .filter(team -> team.getRole().equals(Role.VOLUNTEER))
                        .forEach(receiver ->
                                notificationService.send(receiver.getUser(), party.getUser(), receiver.getParty(), NotificationType.REVIEW, "방장에 대한 리뷰를 남겨주세요.", "리뷰 요청")
                        );
            }
        }
        log.info("=== party 상태 "+ changeCount +"개 변경 완료 ===");
    }
}
