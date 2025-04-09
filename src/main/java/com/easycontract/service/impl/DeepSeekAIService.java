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
    public ChatResponse generateText(String prompt) {
        ChatRequest request = createChatRequest(prompt, false);

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
    }

    @Override
    public Flux<String> generateTextStream(String prompt) {
        ChatRequest request = createChatRequest(prompt, true);

        return webClient.post()
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(ChatStreamResponse.class)
                .map(response -> {
                    if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                        ChatStreamResponse.Delta delta = response.getChoices().get(0).getDelta();
                        return delta != null && delta.getContent() != null ? delta.getContent() : "";
                    }
                    return "";
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
