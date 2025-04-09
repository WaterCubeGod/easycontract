package com.easycontract.entity.ai;

import lombok.Data;

@Data
public class Message {
    private String role; // 如 "user", "assistant", "system"
    private String content;
}
