package com.example.report.util;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("serial")
@Getter
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    int code;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }
}