package com.easycontract.service;

import com.easycontract.entity.ai.ChatResponse;
import com.easycontract.service.impl.DeepSeekAIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test") // 使用测试配置文件
public class AIServiceTest {

    @Autowired
    private AIService aiService;

    @Test
    public void testGenerateText() {
        // 注意：这个测试需要有效的API密钥才能通过
        // 如果没有有效的API密钥，可以注释掉或者使用@Disabled注解
        String prompt = "你好，请介绍一下自己";
        ChatResponse response = aiService.generateText(prompt);
        
        assertNotNull(response);
        assertNotNull(response.getChoices());
        assertNotNull(response.getChoices().get(0).getMessage().getContent());
        
        System.out.println("AI回复: " + response.getChoices().get(0).getMessage().getContent());
    }
}
