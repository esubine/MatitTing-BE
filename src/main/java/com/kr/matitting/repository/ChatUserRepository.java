package com.kr.matitting.repository;

import com.kr.matitting.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

//    @Query("select cu from ChatUser cu " +
//            "join fetch cu.chatRoom " +
//            "where cu.user.id = :userId and cu.roomType = :roomType")
//    List<ChatUser> findByUserIdAndRoomTypeFJRoom(@Param("userId") Long userId,
//                                                 @Param("roomType") ChatRoomType roomType,
//                                                 Pageable pageable);

    Optional<ChatUser> findByUserIdAndChatRoomId(Long userId, Long roomId);
    @Query("select cu from ChatUser cu " +
            "join fetch cu.chatRoom " +
            "where cu.chatRoom.party.id = :partyId")
    Optional<ChatUser> findByPartyIdFJChatRoom(@Param("partyId") Long partyId);

//    @Query("select cu from ChatUser cu " +
//            "join fetch cu.chatRoom " +
//            "where cu.chatRoom.id in :roomIds")
//    List<ChatUser> findByChatRoomIdFJRoom(@Param("roomId") List<Long> roomIds);

    List<ChatUser> findByChatRoomId(Long roomId);
    List<ChatUser> findByUserId(Long userId);

    @Query("select cu from ChatUser cu " +
            "join fetch cu.chatRoom " +
            "where cu.id = :chatUserId and cu.chatRoom.id = :roomId")
    Optional<ChatUser> findByIdAndChatRoom(Long chatUserId, Long roomId);
}
