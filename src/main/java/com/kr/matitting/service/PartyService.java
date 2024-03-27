package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.*;
import com.kr.matitting.exception.Map.MapException;
import com.kr.matitting.exception.Map.MapExceptionType;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.partyjoin.PartyJoinException;
import com.kr.matitting.exception.partyjoin.PartyJoinExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kr.matitting.dto.ChatRoomDto.CreateRoomEvent;
import static com.kr.matitting.dto.ChatRoomDto.JoinRoomEvent;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {
    @Value("${cloud.aws.s3.url}")
    private String url;
    private final PartyJoinRepository partyJoinRepository;
    private final PartyTeamRepository teamRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final MapService mapService;
    private final NotificationService notificationService;
    private final ChatService chatService;

    public ResponsePartyDetailDto getPartyInfo(User user, Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        increaseHit(partyId);

        return ResponsePartyDetailDto.from(party, user);
    }

    @Transactional
    public void increaseHit(Long partyId) {
        partyRepository.increaseHit(partyId);
    }

    public ResponseCreatePartyDto createParty(User user, PartyCreateDto request) {
        log.info("=== createParty() start ===");

        checkParticipant(request.getTotalParticipant());

        // address 변환, deadline, thumbnail이 null일 경우 처리하는 로직 처리 후 생성
        Party party = createBasePartyBuilder(request, user);
        Party savedParty = partyRepository.save(party);

        Team team = new Team(user, savedParty, Role.HOST);
        teamRepository.save(team);

        // 채팅방 생성
        CreateRoomEvent createRoomEvent = new CreateRoomEvent(savedParty.getId(), user.getId());
        ChatRoom createdChatRoom = chatService.createChatRoom(createRoomEvent);

        return new ResponseCreatePartyDto(savedParty, createdChatRoom);
    }

    private Point2D.Double setLocationFunc(double latitude, double longitude) {
        Point2D.Double now = new Point2D.Double();
        now.setLocation(latitude, longitude);
        return now;
    }

    private void checkParticipant(int totalParticipant) {
        if (totalParticipant < 2) {
            throw new PartyException(PartyExceptionType.NOT_MINIMUM_PARTICIPANT);
        }
    }

    private String getThumbnail(PartyCategory category, String thumbnail) {

        if (thumbnail == null) {
            switch (category) {
                case KOREAN -> thumbnail = url + "korean.jpeg";
                case WESTERN -> thumbnail = url + "western.jpeg";
                case CHINESE -> thumbnail = url + "chinese.jpeg";
                case JAPANESE -> thumbnail = url + "japanese.jpeg";
                case ETC -> thumbnail = url + "etc.jpeg";
            }
        }
        return thumbnail;
    }

    private LocalDateTime getDeadline(LocalDateTime deadline, LocalDateTime partyTime) {
        if (deadline == null) {
            deadline = partyTime.minusHours(1L);
        }
        return deadline;
    }

    private String getAddress(double longitude, double latitude) {
        String address = mapService.coordToAddr(longitude, latitude);
        if (address.equals("")) {
            throw new MapException(MapExceptionType.NOT_FOUND_ADDRESS);
        }
        return address;
    }

    public void partyUpdate(User user, PartyUpdateDto partyUpdateDto, Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        checkRole(user, party);
        if (partyUpdateDto.partyTitle() != null) {
            party.setPartyTitle(partyUpdateDto.partyTitle());
        }
        if (partyUpdateDto.partyContent() != null) {
            party.setPartyContent(partyUpdateDto.partyContent());
        }
        if (partyUpdateDto.menu() != null) {
            party.setMenu(partyUpdateDto.menu());
        }
        if (partyUpdateDto.longitude() != null && partyUpdateDto.latitude() != null) {
            party.setLatitude(partyUpdateDto.latitude());
            party.setLongitude(partyUpdateDto.longitude());
            String address = mapService.coordToAddr(partyUpdateDto.longitude(), partyUpdateDto.latitude());
            party.setAddress(address);
        }
        if (partyUpdateDto.partyPlaceName() != null) {
            party.setPartyPlaceName(partyUpdateDto.partyPlaceName());
        }
        if (partyUpdateDto.status() != null) {
            party.setStatus(partyUpdateDto.status());
        }
        if (partyUpdateDto.thumbnail() != null) {
            party.setThumbnail(partyUpdateDto.thumbnail());
        }
        if (partyUpdateDto.partyTime() != null) {
            party.setPartyTime(partyUpdateDto.partyTime());
            party.setDeadline(partyUpdateDto.partyTime().minusHours(1));
        }
        if (partyUpdateDto.totalParticipant() != null) {
            if (party.getParticipantCount() <= partyUpdateDto.totalParticipant()) {
                party.setTotalParticipant(partyUpdateDto.totalParticipant());
            } else {
                throw new PartyException(PartyExceptionType.INVALID_UPDATE_VALUE);
            }
        }
        if (partyUpdateDto.gender() != null) {
            party.setGender(partyUpdateDto.gender());
        }
        if (partyUpdateDto.age() != null) {
            party.setAge(partyUpdateDto.age());
        }
    }

    public void deleteParty(User user, Long partyId) {
        Party party = partyRepository.findById(partyId).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));

        checkRole(user, party);
        partyRepository.delete(party);
    }

    private void checkRole(User user, Party party) {
        if (!user.getId().equals(party.getUser().getId())) {
            throw new UserException(UserExceptionType.INVALID_ROLE_USER);
        }
    }

    // address, deadline, thumbnail와 같이 변환이나 null인 경우 처리가 필요한 필드는 제외하고 나머지 필드는 빌더패턴으로 생성
    private Party createBasePartyBuilder(PartyCreateDto request, User user) {
        return Party.builder()
                .partyTitle(request.getPartyTitle())
                .partyContent(request.getPartyContent())
                .partyPlaceName(request.getPartyPlaceName())
                .latitude(setLocationFunc(request.getLatitude(), request.getLongitude()).x)
                .longitude(setLocationFunc(request.getLatitude(), request.getLongitude()).y)
                .partyTime(request.getPartyTime())
                .totalParticipant(request.getTotalParticipant())
                .gender(request.getGender())
                .age(request.getAge())
                .menu(request.getMenu())
                .category(request.getCategory())
                .status(PartyStatus.RECRUIT)
                .user(user)
                .address(getAddress(request.getLongitude(), request.getLatitude()))
                .deadline(request.getPartyTime().minusHours(1))
                .thumbnail(getThumbnail(request.getCategory(), request.getThumbnail()))
                .build();
    }

    public ResponsePartyJoinDto joinParty(PartyJoinDto partyJoinDto, User user) {
        log.info("=== joinParty() start ===");

        Party party = partyRepository.findById(partyJoinDto.partyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        if (party.getUser().getId().equals(user.getId())) throw new UserException(UserExceptionType.INVALID_ROLE_USER);

        Optional<PartyJoin> existingJoin = partyJoinRepository.findByPartyIdAndLeaderIdAndUserId(party.getId(), party.getUser().getId(), user.getId());
        if (partyJoinDto.status() == PartyJoinStatus.APPLY) {
            if (existingJoin.isPresent()) {
                throw new PartyJoinException(PartyJoinExceptionType.DUPLICATION_PARTY_JOIN);
            }
            PartyJoin savedpartyJoin = partyJoinRepository.save(new PartyJoin(party, party.getUser().getId(), user.getId()));
            notificationService.send(user, NotificationType.PARTICIPATION_REQUEST, "파티 신청", party.getPartyTitle() + " 파티에 참가 신청이 도착했어요.");

            return new ResponsePartyJoinDto(savedpartyJoin.getId());
        } else if (partyJoinDto.status() == PartyJoinStatus.CANCEL) {
            if (existingJoin.isEmpty()) {
                throw new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN);
            }
            partyJoinRepository.delete(existingJoin.get());
            return new ResponsePartyJoinDto(existingJoin.get().getId());
        }
        return null;
    }

    public String decideUser(PartyDecisionDto partyDecisionDto, User user) {
        log.info("=== decideUser() start ===");

        User volunteerUser = userRepository.findByNickname(partyDecisionDto.getNickname()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        PartyJoin findPartyJoin = partyJoinRepository.findByPartyIdAndUserId(
                partyDecisionDto.getPartyId(),
                volunteerUser.getId()).orElseThrow(() -> new PartyJoinException(PartyJoinExceptionType.NOT_FOUND_PARTY_JOIN));

        if (!findPartyJoin.getLeaderId().equals(user.getId()))
            throw new UserException(UserExceptionType.INVALID_ROLE_USER);

        partyJoinRepository.delete(findPartyJoin);

        Party party = partyRepository.findById(partyDecisionDto.getPartyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));
        if (partyDecisionDto.getStatus() == PartyDecision.ACCEPT) {
            log.info("=== ACCEPT ===");

            party.increaseUser();
            Team member = new Team(volunteerUser, party, Role.VOLUNTEER);
            teamRepository.save(member);

            //TODO: 이것 또한 party와 user를 매개변수로 보내는 것이 좋다고 생각합니다! 검증돈 객체를 매개변수로 보내 불필요한 findBy를 줄일 수 있다고 생각합니다.
            // 참가신청 수락된 유저 ~ 채팅방에 초대
            chatService.addParticipant(new JoinRoomEvent(party.getId(), volunteerUser.getId()));

            notificationService.send(volunteerUser, NotificationType.REQUEST_DECISION, "참가신청 여부", party.getPartyTitle() + "파티에 참가 되셨습니다.");
            return "Accept Request Completed";
        } else {
            log.info("=== REFUSE ===");
            notificationService.send(volunteerUser, NotificationType.REQUEST_DECISION, "참가신청 여부", party.getPartyTitle() + "파티에 참가가 거절되었습니다.");
            return "Refuse Request Completed";
        }
    }

    public List<InvitationRequestDto> getJoinList(User user, Role role) {
        List<PartyJoin> partyJoinList;

        switch (role) {
            case HOST:
                partyJoinList = partyJoinRepository.findAllByLeaderId(user.getId());
                return partyJoinList.stream()
                        .map(partyJoin -> {
                            User volunteerUser = userRepository.findById(partyJoin.getUserId())
                                    .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
                            return InvitationRequestDto.toDto(partyJoin, volunteerUser, role);
                        })
                        .toList();
            case VOLUNTEER:
                partyJoinList = partyJoinRepository.findAllByUserId(user.getId());
                return partyJoinList.stream()
                        .map(partyJoin -> {
                            User leaderUser = userRepository.findById(partyJoin.getLeaderId())
                                    .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
                            return InvitationRequestDto.toDto(partyJoin, leaderUser, role);
                        })
                        .toList();
            default:
                throw new UserException(UserExceptionType.INVALID_ROLE_USER);
        }
    }
}
