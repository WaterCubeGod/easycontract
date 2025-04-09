package com.easycontract.entity.ai;

import java.util.List;
import lombok.Data;

@Data
public class ChatResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }
    
    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
