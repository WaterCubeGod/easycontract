package com.easycontract.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AIConfig {

    private DeepSeek deepseek = new DeepSeek();

    @Data
    public static class DeepSeek {
        private String apiKey;
        private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
        private String model = "deepseek-chat";
        private double temperature = 0.7;
        private int maxTokens = 2000;
        private int maxContextTokens = 32000; // deepseek-chat 模型的最大上下文长度
    }
}
