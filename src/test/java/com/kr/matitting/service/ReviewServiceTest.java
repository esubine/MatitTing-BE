package com.kr.matitting.service;

import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.ReviewType;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.reivew.ReviewException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.ReviewRepository;
import com.kr.matitting.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kr.matitting.constant.Gender.FEMALE;
import static com.kr.matitting.constant.Gender.MALE;
import static com.kr.matitting.constant.PartyCategory.WESTERN;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private PartyService partyService;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EntityManager entityManager;

    public User user1;
    public User user2;
    public User user3;
    public User user4;
    public Party party1;

    @BeforeEach
    void setup() {
        User user1 = User.builder()
                .socialId("1123213321")
                .oauthProvider(OauthProvider.NAVER)
                .email("test@naver.com")
                .nickname("새싹개발자")
                .age(20)
                .imgUrl("왈왈.jpg")
                .gender(MALE)
                .role(Role.USER)
                .receivedReviews(new ArrayList<>())
                .sendReviews(new ArrayList<>())
                .build();
        user1.setReceivedReviews(new ArrayList<>());
        user1.setSendReviews(new ArrayList<>());
        this.user1 = userRepository.save(user1);

        User user2 = User.builder()
                .socialId("113929292")
                .oauthProvider(OauthProvider.KAKAO)
                .email("test@kakao.com")
                .nickname("잔디개발자")
                .age(30)
                .imgUrl("야옹.jpg")
                .gender(FEMALE)
                .role(Role.USER)
                .build();
        user2.setReceivedReviews(new ArrayList<>());
        user2.setSendReviews(new ArrayList<>());
        this.user2 = userRepository.save(user2);

        User user3 = User.builder()
                .socialId("0051515")
                .oauthProvider(OauthProvider.KAKAO)
                .email("test3@kakao.com")
                .nickname("불개발자")
                .age(60)
                .imgUrl("음모오.jpg")
                .gender(MALE)
                .role(Role.USER)
                .build();
        user3.setReceivedReviews(new ArrayList<>());
        user3.setSendReviews(new ArrayList<>());
        this.user3 = userRepository.save(user3);

        User user4 = User.builder()
                .socialId("76877778878")
                .oauthProvider(OauthProvider.KAKAO)
                .email("test4@kakao.com")
                .nickname("물개발자")
                .age(15)
                .imgUrl("어흥.jpg")
                .gender(FEMALE)
                .role(Role.USER)
                .build();
        user4.setReceivedReviews(new ArrayList<>());
        user4.setSendReviews(new ArrayList<>());
        this.user4 = userRepository.save(user4);

        PartyCreateDto partyCreateDto = PartyCreateDto
                .builder()
                .partyTitle("테스트 제목")
                .partyContent("테스트 내용")
                .partyPlaceName("가산 인크커피")
                .partyTime(LocalDateTime.now().minusHours(3))
                .totalParticipant(4)
                .longitude(126.88453591058602)
                .latitude(37.53645109566274)
                .gender(MALE)
                .category(WESTERN)
                .age(PartyAge.ALL)
                .menu("커피")
                .thumbnail(null)
                .build();
        ResponseCreatePartyDto party = partyService.createParty(user1, partyCreateDto);
        this.party1 = partyRepository.findById(party.getPartyId()).get();
    }

    public ResponseCreatePartyDto partyCreate(User user) {
        PartyCreateDto partyCreateDto = PartyCreateDto
                .builder()
                .partyTitle("테스트 제목")
                .partyContent("테스트 내용")
                .partyPlaceName("가산 인크커피")
                .partyTime(LocalDateTime.now().minusHours(3))
                .totalParticipant(4)
                .longitude(126.88453591058602)
                .latitude(37.53645109566274)
                .gender(MALE)
                .category(WESTERN)
                .age(PartyAge.ALL)
                .menu("커피")
                .thumbnail(null)
                .build();
        return partyService.createParty(user, partyCreateDto);
    }

    @DisplayName("후기 생성 성공")
    @Test
    void 후기_생성_성공() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");

        //when
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq, user2);
        Optional<Review> reviewById = reviewRepository.findById(review.getReviewId());

        //then
        assertThat(reviewById).isPresent();
        assertThat(reviewById.get().getParty().getId()).isEqualTo(party1.getId());
        assertThat(reviewById.get().getRating()).isEqualTo(50);
        assertThat(reviewById.get().getImgUrl()).isEqualTo("추억사진.jpg");
        assertThat(reviewById.get().getReviewer().getNickname()).isEqualTo("잔디개발자");
        assertThat(reviewById.get().getReceiver().getNickname()).isEqualTo("새싹개발자");
    }

    @DisplayName("후기 생성 실패 - 잘못된 파티 ID")
    @Test
    void 후기_생성_실패_잘못된_파티ID() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId() + 100L, "방장님 멋져요.", 50, "추억사진.jpg");

        //when, then
        assertThrows(PartyException.class, () -> reviewService.createReview(reviewCreateReq, user2));
    }

    @DisplayName("후기 생성 실패 - 유저 없음")
    @Test
    void 후기_생성_실패_유저없음() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId() + 100, party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");

        //when, then
        assertThrows(UserException.class, () -> reviewService.createReview(reviewCreateReq, user2));
    }

    @DisplayName("후기 생성 실패 - 시작하지 않음")
    @Test
    void 후기_생성_실패_시작안함() {
        //given
        Party byId = partyRepository.findById(party1.getId()).get();
        byId.setPartyTime(LocalDateTime.now().plusDays(3));
        entityManager.flush();
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");

        //when, then
        assertThrows(ReviewException.class, () -> reviewService.createReview(reviewCreateReq, user2));
    }

    @DisplayName("후기 생성 실패 - 중복 등록")
    @Test
    void 후기_생성_실패_중복등록() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq, user2);

        //when, then
        assertThrows(ReviewException.class, () -> reviewService.createReview(reviewCreateReq, user2));
    }

    @DisplayName("후기 수정 성공")
    @Test
    void 후기_수정_성공() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq, user2);
        ReviewUpdateReq reviewUpdateReq = new ReviewUpdateReq(review.getReviewId(), "방장님 이뻐요.", 80, "이쁜사진.jpg");

        //when
        reviewService.updateReview(reviewUpdateReq, user2);
        entityManager.flush();
        Optional<Review> findReview = reviewRepository.findById(review.getReviewId());

        //then
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getContent()).isEqualTo("방장님 이뻐요.");
        assertThat(findReview.get().getRating()).isEqualTo(80);
        assertThat(findReview.get().getImgUrl()).isEqualTo("이쁜사진.jpg");
    }

    @DisplayName("후기 수정 실패 - Role 위반")
    @Test
    void 후기_수정_실패_Role위반() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq, user2);
        ReviewUpdateReq reviewUpdateReq = new ReviewUpdateReq(review.getReviewId(), "방장님 이뻐요.", 80, "이쁜사진.jpg");

        //when, then
        assertThrows(UserException.class, () -> reviewService.updateReview(reviewUpdateReq, user1));
    }

    @DisplayName("후기 삭제 성공")
    @Test
    void 후기_삭제_성공() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq, user2);
        ReviewDeleteReq reviewDeleteReq = new ReviewDeleteReq(review.getReviewId());

        //when
        reviewService.deleteReview(reviewDeleteReq, user2);
        Optional<Review> byId = reviewRepository.findById(review.getReviewId());

        //then
        assertThat(byId).isEmpty();
    }

    @DisplayName("후기 삭제 실패 - ROle 위반")
    @Test
    void 후기_삭제_실패_Role위반() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq, user2);
        ReviewDeleteReq reviewDeleteReq = new ReviewDeleteReq(review.getReviewId());

        //when, then
        assertThrows(UserException.class, () -> reviewService.deleteReview(reviewDeleteReq, user1));
    }

    @DisplayName("후기 리스트 조회 성공")
    @Test
    void 후기_리스트_조회_성공() {
        //given
        ReviewCreateReq reviewCreateReq1 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요1.", 10, "추억사진1.jpg");
        ReviewCreateReq reviewCreateReq2 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요2.", 20, "추억사진2.jpg");
        ReviewCreateReq reviewCreateReq3 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요3.", 30, "추억사진3.jpg");
        reviewService.createReview(reviewCreateReq1, user2);
        reviewService.createReview(reviewCreateReq2, user3);
        reviewService.createReview(reviewCreateReq3, user4);

        //when
        List<ReviewGetRes> reviewList = reviewService.getReviewList(user1, ReviewType.RECEIVER);
        List<ReviewGetRes> reviewList1 = reviewService.getReviewList(user2, ReviewType.SENDER);
        List<ReviewGetRes> reviewList2 = reviewService.getReviewList(user3, ReviewType.SENDER);
        List<ReviewGetRes> reviewList3 = reviewService.getReviewList(user4, ReviewType.SENDER);

        //then
        assertThat(reviewList.size()).isEqualTo(3);
        assertThat(reviewList.stream().mapToDouble(ReviewGetRes::getRating).average().getAsDouble()).isEqualTo(20.0);
        assertThat(reviewList1.size()).isEqualTo(1);
        assertThat(reviewList1.stream().mapToDouble(ReviewGetRes::getRating).average().getAsDouble()).isEqualTo(10.0);
        assertThat(reviewList2.size()).isEqualTo(1);
        assertThat(reviewList2.stream().mapToDouble(ReviewGetRes::getRating).average().getAsDouble()).isEqualTo(20.0);
        assertThat(reviewList3.size()).isEqualTo(1);
        assertThat(reviewList3.stream().mapToDouble(ReviewGetRes::getRating).average().getAsDouble()).isEqualTo(30.0);
    }

    @DisplayName("후기 리스트 조회 실패 - 잘못된 매개변수")
    @Test
    void 후기_리스트_조회_실패_잘못된_매개변수() {
        //given
        ReviewCreateReq reviewCreateReq1 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요1.", 10, "추억사진1.jpg");
        ReviewCreateReq reviewCreateReq2 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요2.", 20, "추억사진2.jpg");
        ReviewCreateReq reviewCreateReq3 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요3.", 30, "추억사진3.jpg");
        reviewService.createReview(reviewCreateReq1, user2);
        reviewService.createReview(reviewCreateReq2, user3);
        reviewService.createReview(reviewCreateReq3, user4);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> reviewService.getReviewList(user1, ReviewType.valueOf("test")));
    }

    @DisplayName("후기 상세 조회 성공")
    @Test
    void 후기_상세조회_성공() {
        //given
        ReviewCreateReq reviewCreateReq1 = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요1.", 10, "추억사진1.jpg");
        ReviewCreateRes review = reviewService.createReview(reviewCreateReq1, user2);

        //when
        ReviewInfoRes findReview = reviewService.getReview(user2, review.getReviewId());
        ReviewInfoRes findReview1 = reviewService.getReview(null, review.getReviewId());

        //then
        assertThat(findReview.getReviewId()).isEqualTo(review.getReviewId());
        assertThat(findReview.getNickname()).isEqualTo(user2.getNickname());
        assertThat(findReview.getContent()).isEqualTo("방장님 멋져요1.");
        assertThat(findReview.getRating()).isEqualTo(10);
        assertThat(findReview.getReviewImg()).isEqualTo("추억사진1.jpg");
        assertThat(findReview.getIsSelfReview()).isTrue();
        assertThat(findReview1.getNickname()).isEqualTo(user2.getNickname());
        assertThat(findReview1.getIsSelfReview()).isFalse();
    }

    @DisplayName("방장 후기 조회 성공")
    @Test
    void 방장_후기조회_성공() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review1 = reviewService.createReview(reviewCreateReq, user2);
        ReviewCreateRes review2 = reviewService.createReview(reviewCreateReq, user3);

        //when
        List<ReviewGetRes> hostReviewList = reviewService.getHostReviewList(user1.getId());

        //then
        assertThat(hostReviewList.size()).isEqualTo(2);
        assertThat(hostReviewList.get(0).getReviewId()).isEqualTo(review2.getReviewId());
        assertThat(hostReviewList.get(1).getReviewId()).isEqualTo(review1.getReviewId());
    }

    @DisplayName("방장 후기 조회 실패 없는 유저ID")
    @Test
    void 방장_후기조회_실패_없는_유저ID() {
        //given
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "방장님 멋져요.", 50, "추억사진.jpg");
        ReviewCreateRes review1 = reviewService.createReview(reviewCreateReq, user2);
        ReviewCreateRes review2 = reviewService.createReview(reviewCreateReq, user3);

        //when, then
        assertThrows(UserException.class, () -> reviewService.getHostReviewList(user1.getId() + 100L));
    }
}


