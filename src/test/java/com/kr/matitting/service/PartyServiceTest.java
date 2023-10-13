package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.entity.*;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.crossstore.ChangeSetPersister.*;

@Slf4j
@Transactional
@SpringBootTest
class PartyServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private PartyJoinRepository partyJoinRepository;
    @Autowired
    private PartyTeamRepository teamRepository;
    @Autowired
    private PartyService partyService;

    @BeforeEach
    public void 데이터생성() {
        User user = User.builder()
                .socialId("30123")
                .email("parksn5029@nate.com")
                .nickname("새싹개발자")
                .age(26)
                .imgUrl("https://www.naver.com")
                .city("전주")
                .role(Role.USER)
                .socialType(SocialType.KAKAO)
                .build();
        userRepository.save(user);

        User guest = User.builder()
                .socialId("12134")
                .email("parkjd5029@gmail.com")
                .nickname("안경잡이개발자")
                .age(26)
                .imgUrl("https://www.google.com")
                .city("전주")
                .role(Role.USER)
                .socialType(SocialType.KAKAO)
                .build();
        userRepository.save(guest);

        Menu menu = Menu.builder().id(1L).menu("돈까스").category(PartyCategory.WESTERN).thumbnail("https:www").build();

        Party party = Party.builder()
                .partyTitle("파티Test")
                .menu(menu)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.of(2023, 10, 12, 15, 23, 32))
                .hit(1)
                .build();
        partyRepository.save(party);

        Team team = Team.builder()
                .user(user)
                .party(party)
                .role(Role.HOST)
                .build();
        teamRepository.save(team);
    }

    public static String title = "파티Test";
    public static String userSocialId = "30123";
    public static String guestSocialId = "12134";

    @Test
    void 파티신청_성공() throws Exception {
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        //when
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.WAIT));

        log.info("=== Party Join Save ===");
        partyService.joinParty(partyJoinDto);

        log.info("=== Party Select by PartyId And ParentId ===");
        Optional<List<PartyJoin>> partyJoinList = partyJoinRepository.findByPartyIdAndLeaderId(partyJoinDto.partyId(), partyJoinDto.leaderId());

        //then
        assertThat(partyJoinList.get().size()).isEqualTo(1);
        assertThat(partyJoinList.get().get(0).getParty().getId()).isEqualTo(partyJoinDto.partyId());
        assertThat(partyJoinList.get().get(0).getLeaderId()).isEqualTo(partyJoinDto.leaderId());
        assertThat(partyJoinList.get().get(0).getUserId()).isEqualTo(partyJoinDto.userId());
    }

    @Test
    void 파티신청_실패_방장Id가_없을때() throws Exception {
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        //when, then
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), 100000000L, guest.get().getId(), Optional.of(PartyJoinStatus.WAIT));

        assertThrows(IllegalStateException.class, () -> {
            partyService.joinParty(partyJoinDto);
        });
    }

    @Test
    void 파티신청_실패_파티Id가_없을때(){
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        //when, then
        PartyJoinDto partyJoinDto = new PartyJoinDto(100000L, user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.WAIT));

        assertThrows(IllegalStateException.class, () -> {
            partyService.joinParty(partyJoinDto);
        });
    }

    @Test
    void 파티신청_실패_사용자Id가_없을때(){
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        //when, then
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), 100000L, Optional.of(PartyJoinStatus.WAIT));

        assertThrows(IllegalStateException.class, () -> {
            partyService.joinParty(partyJoinDto);
        });
    }

    @Test
    void 파티요청_수락_성공() throws Exception {
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        log.info("=== Party Join Save ===");
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.ACCEPT));
        partyService.joinParty(partyJoinDto);

        //when
        String result = partyService.decideUser(partyJoinDto);
        Optional<List<Team>> findTeams = teamRepository.findByPartyId(findParty.get().getId());

        //then
        assertThat(result).isEqualTo("Accept Request Completed");
        assertThat(findTeams.get().size()).isEqualTo(2);
    }

    @Test
    void 파티요청_수락_실패_상태대기() throws Exception {
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        log.info("=== Party Join Save ===");
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.WAIT));
        partyService.joinParty(partyJoinDto);

        //when, then
        assertThrows(IllegalStateException.class, () -> partyService.decideUser(partyJoinDto));
    }

    @Test
    void 파티요청_수락_실패_파티없음() throws Exception {
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);

        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        //when
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.ACCEPT));
        partyService.joinParty(partyJoinDto);
        partyRepository.delete(findParty.get());

        //then
        assertThrows(NotFoundException.class, () -> partyService.decideUser(partyJoinDto));
    }

    @Test
    void 파티요청_수락_실패_유저없음() throws Exception {
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);


        //when
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.ACCEPT));
        partyService.joinParty(partyJoinDto);
        userRepository.delete(user.get());
        userRepository.delete(guest.get());

        //then
        assertThrows(NotFoundException.class, () -> partyService.decideUser(partyJoinDto));
    }

    @Test
    void 파티요청_거절_성공() throws Exception{
        //given
        Optional<Party> findParty = partyRepository.findByPartyTitle(title);
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        Optional<User> user = userRepository.findBySocialId(userSocialId);

        log.info("=== Party Join Save ===");
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.get().getId(), guest.get().getId(), Optional.of(PartyJoinStatus.REFUSE));
        partyService.joinParty(partyJoinDto);

        //when
        String result = partyService.decideUser(partyJoinDto);
        Optional<List<Team>> findTeams = teamRepository.findByPartyId(findParty.get().getId());

        //then
        assertThat(result).isEqualTo("Refuse Request Completed");
        assertThat(findTeams.get().size()).isEqualTo(1);
    }
}