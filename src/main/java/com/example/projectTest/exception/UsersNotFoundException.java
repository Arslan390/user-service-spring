package com.example.projectTest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsersNotFoundException extends RuntimeException {

    public UsersNotFoundException() {
        super("Список пользователей пуст");
    }
}
