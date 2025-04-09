package com.easycontract.entity.ai;

import java.util.List;
import lombok.Data;

@Data
public class ChatRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;
    private boolean stream;
}
