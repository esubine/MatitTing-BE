package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UserSignUpDto(
        @Schema(description = "사용자 ID", example = "1")
        @NotNull
        Long userId,
        @Schema(description = "별명", example = "새싹개발자")
        @NotBlank
        String nickname,
        @Schema(description = "생년월일", example = "2024-02-13")
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthday,
        @Schema(description = "성별", example = "MALE")
        @NotNull
        Gender gender
) {
}
