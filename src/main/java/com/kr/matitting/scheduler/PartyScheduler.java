package com.kr.matitting.scheduler;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.entity.Party;
import com.kr.matitting.repository.PartyRepository;
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

    @Scheduled(cron = "0 0 */1 * * ?", zone = "Asia/Seoul")
    public void checkEndParty() {

        log.info("=== party 상태 변경 스케줄러 시작 ===");

        LocalDateTime overedPartyTime = LocalDateTime.now().minusHours(5);

        List<Party> partyList = partyRepository.findByStatus(PartyStatus.RECRUIT);

        for (Party party : partyList) {
            if (party.getPartyTime().isBefore(overedPartyTime) && party.getStatus() == PartyStatus.RECRUIT) {
                party.setStatus(PartyStatus.FINISH);
                partyRepository.save(party);
            }
        }
    }
}
