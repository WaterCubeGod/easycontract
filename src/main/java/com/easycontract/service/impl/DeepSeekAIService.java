package com.easycontract.service.impl;

import com.easycontract.configuration.AIConfig;
import com.easycontract.entity.ai.*;
import com.easycontract.service.AIService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeepSeekAIService implements AIService {

    private final WebClient webClient;
    private final AIConfig aiConfig;

    public DeepSeekAIService(AIConfig aiConfig) {
        this.aiConfig = aiConfig;
        this.webClient = WebClient.builder()
                .baseUrl(aiConfig.getDeepseek().getApiUrl())
                .defaultHeader("Authorization", "Bearer " + aiConfig.getDeepseek().getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }



    @Override
    public Flux<String> generateText(String prompt) {
        ChatRequest request = createChatRequest(prompt, true);

        return webClient.post()
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                // 使用String类型接收原始数据，而不是尝试反序列化为ChatStreamResponse
                .bodyToFlux(String.class)
                .onErrorResume(e -> {
                    // 当发生错误时，返回一个包含错误信息的Flux
                    System.err.println("流式输出错误: " + e.getMessage());
                    e.printStackTrace();
                    // 返回一个空的Flux，不影响前面已经发送的数据
                    return Flux.empty();
                })
                .map(rawResponse -> {
                    try {

                        // 尝试从原始响应中提取文本内容
                        if (rawResponse != null && !rawResponse.isEmpty()) {
                            // 如果是[DONE]消息或其他特殊消息，返回空字符串
                            if (rawResponse.contains("[DONE]")) {
                                return "";
                            }

                            // 检查是否包含"content"字段
                            if (rawResponse.contains("\"content\":")) {
                                // 简单提取content字段的值
                                int contentIndex = rawResponse.indexOf("\"content\":");
                                if (contentIndex >= 0) {
                                    int valueStart = rawResponse.indexOf('"', contentIndex + 10) + 1;
                                    int valueEnd = rawResponse.indexOf('"', valueStart);
                                    if (valueStart > 0 && valueEnd > valueStart) {
                                        String content = rawResponse.substring(valueStart, valueEnd);
                                        return content;
                                    }
                                }
                            }

                            // 如果数据是纯文本，直接返回
                            if (!rawResponse.startsWith("{") && !rawResponse.startsWith("[")) {
                                System.out.println("纯文本内容: " + rawResponse);
                                return rawResponse;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("解析流式响应错误: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return ""; // 默认返回空字符串
                });
    }

    private ChatRequest createChatRequest(String prompt, boolean stream) {
        ChatRequest request = new ChatRequest();
        request.setModel(aiConfig.getDeepseek().getModel());
        request.setTemperature(aiConfig.getDeepseek().getTemperature());
        request.setMax_tokens(aiConfig.getDeepseek().getMaxTokens());
        request.setStream(stream);

        List<Message> messages = new ArrayList<>();
        Message userMessage = new Message();
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        messages.add(userMessage);

        request.setMessages(messages);
        return request;
    }
}
