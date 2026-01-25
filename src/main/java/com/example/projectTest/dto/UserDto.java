package com.example.projectTest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для представления пользователя в API")
public class UserDto extends RepresentationModel<UserDto> {

    @Schema(
            description = "Уникальный идентификатор пользователя",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Электронная почта пользователя",
            example = "user@example.com"
    )
    private String email;

    @Schema(
            description = "Полное имя пользователя",
            example = "Иван"
    )
    private String name;

    @Schema(
            description = "Возраст пользователя в годах",
            example = "25",
            minimum = "0",
            maximum = "100"
    )
    private Integer age;

    @Schema(
            description = "Дата создания записи",
            example = "2023-10-05T10:15:30Z",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant createdAt;
}