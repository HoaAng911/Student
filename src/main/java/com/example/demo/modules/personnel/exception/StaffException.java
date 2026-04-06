package com.example.demo.modules.personnel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception cho module Nhân sự.
 * Trả về mã lỗi 400 (Bad Request) khi có lỗi logic nghiệp vụ.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StaffException extends RuntimeException {

    public StaffException(String message) {
        super(message);
    }

    public StaffException(String message, Throwable cause) {
        super(message, cause);
    }
}