package com.example.projectTest.mapper;

import com.example.projectTest.dto.CreateUserDto;
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

    public User toEntity(CreateUserDto createUserDto) {
        return User.builder()
                .email(createUserDto.getEmail())
                .name(createUserDto.getName())
                .age(createUserDto.getAge())
                .build();
    }
}