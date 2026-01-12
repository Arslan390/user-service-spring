package com.example.projectTest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Список пользователей пуст")
public class EmptyListException extends RuntimeException {
    public EmptyListException() {
        super("Список пользователей пуст");
    }
}
