package com.kr.matitting.dto;

import java.util.Optional;

public record MemberUpdateDto(
        Optional<String> nickname,
        Optional<Integer> age,
        Optional<String> imgUrl) {
}
