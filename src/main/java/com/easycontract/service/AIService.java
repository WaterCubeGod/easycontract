package com.easycontract.service;

import com.easycontract.entity.enums.ContractEnum;
import com.easycontract.entity.es.ChatConversation;
import com.easycontract.entity.vo.MessageRequest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AIService {

    /**
     * 生成文本（流式响应）
     * @param prompt 提示词
     * @return 文本流
     */
    Flux<String> generateText(String prompt);


    Flux<String> responseText(MessageRequest request, String conversationId, String parentMessageId);
}
