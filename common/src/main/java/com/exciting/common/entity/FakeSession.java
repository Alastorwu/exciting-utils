package com.exciting.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FakeSession {
    private LocalDateTime createTime;
    private String sessionValue;
    private LocalDateTime expireTime;

}
