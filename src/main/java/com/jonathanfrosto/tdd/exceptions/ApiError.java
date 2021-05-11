package com.jonathanfrosto.tdd.exceptions;

import lombok.Getter;

@Getter
public class ApiError {
    private final String field;
    private final String message;

    public ApiError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public ApiError(String message) {
        this.field = null;
        this.message = message;
    }
}
