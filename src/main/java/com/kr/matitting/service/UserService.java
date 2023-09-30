package com.kr.matitting.service;

import com.kr.matitting.entity.User;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String signUp(User user) {
        User createdUser = userRepository.save(user);
        return createdUser.getEmail();
    }
}
