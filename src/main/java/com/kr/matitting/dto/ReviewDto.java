package com.kr.matitting.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long userId;
    private String content;
}
