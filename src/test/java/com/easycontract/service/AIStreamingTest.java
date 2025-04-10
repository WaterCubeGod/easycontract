package com.easycontract.service;

import com.easycontract.entity.ai.ChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // 使用测试配置文件
public class AIStreamingTest {

    @Autowired
    private AIService aiService;

    @Test
    public void testGenerateTextStream() {
        // 注意：这个测试需要有效的API密钥才能通过
        // 如果没有有效的API密钥，可以注释掉或者使用@Disabled注解
        String prompt = "你好，请用5个字介绍一下自己";
        
        // 用于收集完整响应
        AtomicReference<StringBuilder> responseCollector = new AtomicReference<>(new StringBuilder());
        
        // 获取流式响应
        Flux<String> responseFlux = aiService.generateTextStream(prompt)
                .doOnNext(chunk -> {
                    // 收集响应内容
                    responseCollector.get().append(chunk);
                    // 打印每个块，便于观察
                    System.out.println("收到块: [" + chunk + "]");
                });
        
        // 验证流式响应
        StepVerifier.create(responseFlux)
                .expectNextCount(1) // 至少应该有一个响应块
                .thenCancel() // 取消订阅，因为我们只想验证流是否正常工作
                .verify(Duration.ofSeconds(10)); // 设置超时时间
        
        // 验证是否收集到了响应
        String collectedResponse = responseCollector.get().toString();
        System.out.println("收集到的响应: " + collectedResponse);
        assertFalse(collectedResponse.isEmpty(), "应该收集到非空响应");
    }
    
    @Test
    public void testCompareStreamAndNonStream() {
        // 比较流式和非流式响应的内容
        String prompt = "用一句话介绍春天";
        
        // 获取非流式响应
        ChatResponse nonStreamResponse = aiService.generateText(prompt);
        String nonStreamContent = nonStreamResponse.getChoices().get(0).getMessage().getContent();
        System.out.println("非流式响应: " + nonStreamContent);
        
        // 获取流式响应
        AtomicReference<StringBuilder> streamResponseCollector = new AtomicReference<>(new StringBuilder());
        aiService.generateTextStream(prompt)
                .doOnNext(chunk -> streamResponseCollector.get().append(chunk))
                .blockLast(Duration.ofSeconds(30)); // 等待流完成
        
        String streamContent = streamResponseCollector.get().toString();
        System.out.println("流式响应: " + streamContent);
        
        // 验证两种响应都不为空
        assertFalse(nonStreamContent.isEmpty(), "非流式响应不应为空");
        assertFalse(streamContent.isEmpty(), "流式响应不应为空");
        
        // 注意：由于AI生成的内容可能有差异，我们不比较具体内容是否相同
        // 但两者都应该是有意义的回答
    }
}
