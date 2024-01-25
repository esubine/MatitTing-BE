package com.kr.matitting.dto;

import lombok.Getter;

@Getter
public class ResponseDummyDataDto {
    String user1_accesstoken;
    String user1_refreshtoken;
    String user2_accesstoken;
    String user2_refreshtoken;
    Long user1_partyId;
    Long user2_partyId;

    public ResponseDummyDataDto(String user1_accesstoken, String user1_refreshtoken,
                                String user2_accesstoken, String user2_refreshtoken, Long user1_partyId, Long user2_partyId){
        this.user1_accesstoken = user1_accesstoken;
        this.user1_refreshtoken = user1_refreshtoken;
        this.user2_accesstoken = user2_accesstoken;
        this.user2_refreshtoken = user2_refreshtoken;
        this.user1_partyId = user1_partyId;
        this.user2_partyId = user2_partyId;
    }

}
