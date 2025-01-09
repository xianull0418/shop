package com.spring.back.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return code == 200;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
} 