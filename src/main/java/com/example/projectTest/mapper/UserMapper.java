package com.example.projectTest.mapper;

import com.example.projectTest.dto.UserDto;
import com.example.projectTest.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toUser(UserDto user) {
        return User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .build();
    }
}
