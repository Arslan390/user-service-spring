package com.example.projectTest.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto extends RepresentationModel<UserDto> {
    private Long id;
    private String email;
    private String name;
    private Integer age;
    private Instant createdAt;
}