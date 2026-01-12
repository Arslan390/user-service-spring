package com.example.projectTest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Email уже зарегистрирован.")
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Пользователь с данным адресом электронной почты уже существует.");
    }
}
