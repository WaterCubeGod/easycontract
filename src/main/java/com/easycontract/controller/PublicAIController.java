package com.easycontract.controller;

import com.easycontract.service.AIService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 公开的AI控制器
 * 专门用于处理不需要认证的AI请求
 */
@RestController
@RequestMapping("/api/public/ai")
@CrossOrigin(origins = "*")
public class PublicAIController {

    private final AIService aiService;

    public PublicAIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * 流式生成文本，不需要认证
     */
    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateTextStream(@RequestBody String prompt) {
        try {
            return aiService.generateTextStream(prompt);
        } catch (Exception e) {
            return Flux.error(new RuntimeException("AI生成失败: " + e.getMessage()));
        }
    }
}
