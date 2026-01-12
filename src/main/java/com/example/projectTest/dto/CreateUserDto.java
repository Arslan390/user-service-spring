package com.example.projectTest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDto {

    @NotBlank(message = "Email обязателен")
    @Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неверный формат электронной почты")
    private String email;

    @NotBlank(message = "Имя обязательно")
    @Size(min=3, message = "Минимальная длина имени - 3 символа")
    private String name;

    @NotNull(message = "Возраст обязателен")
    @Min(value=0, message = "Возраст должен быть больше или равен нулю")
    @Max(value=100, message = "Максимально допустимый возраст - 100 лет")
    private Integer age;
}