package com.kr.matitting.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;
public record UserUpdateDto (
    @NotNull
    Long userId,
    Optional<String> nickname,
    Optional<String> imgUrl){

}

