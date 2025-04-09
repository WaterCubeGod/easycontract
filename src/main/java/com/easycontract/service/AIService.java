package com.easycontract.service;

import com.easycontract.entity.ai.ChatResponse;
import reactor.core.publisher.Flux;

public interface AIService {
    ChatResponse generateText(String prompt);
    Flux<String> generateTextStream(String prompt);
}
