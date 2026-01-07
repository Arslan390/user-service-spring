package com.example.projectTest.util;

public class ValidationUtils {

    public static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Поле 'email' обязательно для заполнения.");
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Недопустимый формат электронной почты");
        }
    }

    public static void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Поле 'имя' обязательно для заполнения.");
        }
        if (name.trim().length() < 3 || name.trim().length() > 50) {
            throw new IllegalArgumentException("Имя должно содержать от 3 до 50 символов");
        }
    }

    public static void validateAge(Integer age) {
        if (age == null) {
            throw new IllegalArgumentException("Поле 'возрост' обязательно для заполнения");
        }
        if (age < 0 || age > 100) {
            throw new IllegalArgumentException("Возраст должен находиться в диапазоне от 0 до 100 лет");
        }
    }
}