package com.exciting.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class FakeSession {
    @Getter @Setter private LocalDateTime createTime;
    @Getter @Setter private String sessionValue;
    @Getter @Setter private LocalDateTime expireTime;

}
