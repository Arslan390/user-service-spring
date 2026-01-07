package com.example.projectTest.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email уже существует");
    }
}
