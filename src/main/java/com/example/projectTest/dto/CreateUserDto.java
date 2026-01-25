package com.example.projectTest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для создания нового пользователя")
public class CreateUserDto {

    @Schema(
            description = "Электронная почта пользователя",
            example = "user@example.com"
    )
    @NotBlank(message = "Email обязателен")
    @Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неверный формат электронной почты")
    private String email;

    @Schema(
            description = "Полное имя пользователя",
            example = "Иван"
    )
    @NotBlank(message = "Имя обязательно")
    @Size(min=3, message = "Минимальная длина имени - 3 символа")
    private String name;

    @Schema(
            description = "Возраст пользователя в годах",
            example = "25",
            minimum = "0",
            maximum = "100"
    )
    @NotNull(message = "Возраст обязателен")
    @Min(value=0, message = "Возраст должен быть больше или равен нулю")
    @Max(value=100, message = "Максимально допустимый возраст - 100 лет")
    private Integer age;
}