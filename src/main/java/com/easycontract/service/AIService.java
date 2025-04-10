package com.easycontract.service;

import reactor.core.publisher.Flux;

public interface AIService {
    Flux<String> generateText(String prompt);
}
