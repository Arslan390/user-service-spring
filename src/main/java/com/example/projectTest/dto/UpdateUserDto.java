package com.example.projectTest.dto;

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
public class UpdateUserDto {

    @Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неверный формат электронной почты")
    private String email;

    @Size(min=3, message = "Минимальная длина имени - 3 символа")
    private String name;

    @Min(value=0, message = "Возраст должен быть больше или равен нулю")
    @Max(value=100, message = "Максимально допустимый возраст - 100 лет")
    private Integer age;
}