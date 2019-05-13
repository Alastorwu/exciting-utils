package com.exciting.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wujiaxing
 * @date 2018/4/23 20:53
 */
@Data

public class ResponseEntity<T> implements Serializable {

    private String code;
    private String message;
    private T data;

}
