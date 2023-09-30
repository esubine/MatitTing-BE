package com.kr.matitting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoDto {
    private Long socialId;
    private String email;
    private String nickname;
    private String gender;
    private String birth;
    private String imgUrl;
}
