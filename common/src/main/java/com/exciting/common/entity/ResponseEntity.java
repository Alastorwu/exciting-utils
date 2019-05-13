package com.exciting.common.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import static org.springframework.http.HttpStatus.*;

@Data
public class ResponseEntity<T>{

    private T body;
    private Integer status;
    private String message;

    public ResponseEntity(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status.value();
    }

    public ResponseEntity(int status) {
        this.status = status;
    }

    public ResponseEntity(HttpStatus status, String message) {
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status.value();
        this.message = message;
    }

    public ResponseEntity(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseEntity(@Nullable T body, HttpStatus status) {
        this(status);
        this.body = body;
    }

    public ResponseEntity(@Nullable T body, int status) {
        this.body = body;
        this.status = status;
    }

    public ResponseEntity(@Nullable T body, HttpStatus status, String message) {
        this(status,message);
        this.body = body;
    }

    public ResponseEntity(@Nullable T body, int status, String message) {
        this(status,message);
        this.body = body;
    }

    public static <T> ResponseEntity<T> ok(@Nullable T body) {
        return new ResponseEntity<>(body, OK);
    }
    public static <T> ResponseEntity<T> serverError(String message) {
        return new ResponseEntity<>(null, INTERNAL_SERVER_ERROR, message);
    }
    public static <T> ResponseEntity<T> forbidden(String message) {
        return new ResponseEntity<>(null, FORBIDDEN, message);
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus.value();
    }
}
