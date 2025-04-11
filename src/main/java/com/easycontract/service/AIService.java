package com.easycontract.service;

import com.easycontract.entity.es.ChatConversation;
import reactor.core.publisher.Flux;

public interface AIService {
    /**
     * 生成文本（流式响应）
     * @param prompt 提示词
     * @return 文本流
     */
    Flux<String> generateText(String prompt);

    /**
     * 构建对话上下文
     * @param conversation 对话对象
     * @param currentMessageId 当前消息ID
     * @return 构建的上下文字符串
     */
    String buildConversationContext(ChatConversation conversation, String currentMessageId);
}
