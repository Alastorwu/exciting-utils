package com.exciting.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import static com.exciting.common.entity.HttpStatus.*;

@Data
@ApiModel()
public class ResEntity<T>{

    @ApiModelProperty(value = "返回实体")
    private T body;
    @ApiModelProperty(value = "返回码")
    private Integer status;
    @ApiModelProperty(value = "描述")
    private String message;

    public ResEntity(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status.value();
    }

    public ResEntity(int status) {
        this.status = status;
    }

    public ResEntity(HttpStatus status, String message) {
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status.value();
        this.message = message;
    }

    public ResEntity(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResEntity(@Nullable T body, HttpStatus status) {
        this(status);
        this.body = body;
    }

    public ResEntity(@Nullable T body, int status) {
        this.body = body;
        this.status = status;
    }

    public ResEntity(@Nullable T body, HttpStatus status, String message) {
        this(status,message);
        this.body = body;
    }

    public ResEntity(@Nullable T body, int status, String message) {
        this(status,message);
        this.body = body;
    }

    public static <T> ResEntity<T> ok(@Nullable T body) {
        return new ResEntity<>(body, OK, OK.getReasonPhrase());
    }
    public static <T> ResEntity<T> serverError(String message) {
        return new ResEntity<>(null, INTERNAL_SERVER_ERROR, message);
    }
    public static <T> ResEntity<T> forbidden(String message) {
        return new ResEntity<>(null, FORBIDDEN, message);
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus.value();
    }
}
