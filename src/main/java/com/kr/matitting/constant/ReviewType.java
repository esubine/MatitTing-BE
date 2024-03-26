package com.kr.matitting.constant;

import com.kr.matitting.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
public enum ReviewType {
    SENDER, RECEIVER
}
