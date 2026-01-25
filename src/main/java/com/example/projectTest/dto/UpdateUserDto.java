package com.example.projectTest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для обновления данных пользователя")
public class UpdateUserDto {

    @Schema(
            description = "Новая электронная почта пользователя",
            example = "new-email@example.com"
    )
    @Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неверный формат электронной почты")
    private String email;

    @Schema(
            description = "Новое полное имя пользователя",
            example = "Пётр"
    )
    @Size(min=3, message = "Минимальная длина имени - 3 символа")
    private String name;

    @Schema(
            description = "Новый возраст пользователя в годах",
            example = "30",
            minimum = "0",
            maximum = "100"
    )
    @Min(value=0, message = "Возраст должен быть больше или равен нулю")
    @Max(value=100, message = "Максимально допустимый возраст - 100 лет")
    private Integer age;
}