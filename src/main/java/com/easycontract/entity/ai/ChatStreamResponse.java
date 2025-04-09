package com.easycontract.entity.ai;

import java.util.List;
import lombok.Data;

@Data
public class ChatStreamResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    
    @Data
    public static class Choice {
        private int index;
        private Delta delta;
        private String finish_reason;
    }
    
    @Data
    public static class Delta {
        private String content;
    }
}
