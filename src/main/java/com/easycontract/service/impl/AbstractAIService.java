package com.easycontract.service.impl;

import com.easycontract.configuration.AIConfig;
import com.easycontract.entity.ai.ChatRequest;
import com.easycontract.entity.ai.Message;
import com.easycontract.entity.es.ChatConversation;
import com.easycontract.entity.vo.MessageRequest;
import com.easycontract.service.AIService;
import com.easycontract.service.ChatConversationService;
import com.easycontract.service.PromptEngineeringService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAIService implements AIService {

    @Autowired
    protected ChatConversationService chatConversationService;

    @Autowired
    protected PromptEngineeringService promptEngineeringService;

    protected final AIConfig aiConfig;

    public AbstractAIService(AIConfig aiConfig) {
        this.aiConfig = aiConfig;
    }

    @Override
    public Flux<String> responseText(MessageRequest request, String conversationId, String parentMessageId) {
        String context = getContext(request.getContent(), conversationId, parentMessageId);
        String prompt = promptEngineeringService.generatePrompt(context, request);
        if (estimateTokenCount(prompt) > aiConfig.getDeepseek().getMaxContextTokens()) {
            // 如果提示词过长，截断上下文
            return Flux.error(new RuntimeException("提示词过长，无法生成响应"));
        }

        return generateText(prompt);
    }


    private String getContext(String content, String conversationId, String parentMessageId) {
        if (parentMessageId == null && conversationId == null) {
            return content;
        }
        ChatConversation chatConversation = chatConversationService.getConversation(conversationId);
        StringBuilder context = new StringBuilder();
        context.append(buildConversationContext(chatConversation, parentMessageId));
        context.append("以下是用户最新对话：");
        context.append(content);

        return context.toString();
    }

    /**
     * 构建对话上下文
     * @param conversation 对话对象
     * @param currentMessageId 当前消息ID
     * @return 构建的上下文字符串（不包含提示词）
     */
    String buildConversationContext(ChatConversation conversation, String currentMessageId) {

        // 存储消息的映射，便于查找
        Map<String, ChatConversation.Message> messagesById = new HashMap<>();
        for (ChatConversation.Message message : conversation.getMessages()) {
            messagesById.put(message.getMessageId(), message);
        }

        // 构建对话历史
        List<ChatConversation.Message> conversationHistory = new ArrayList<>();
        String messageId = currentMessageId;

        // 从当前消息开始，向上追溯对话历史
        while (messageId != null) {
            ChatConversation.Message message = messagesById.get(messageId);
            if (message == null) break;

            // 将消息添加到历史记录的开头（保持时间顺序）
            conversationHistory.add(0, message);

            // 获取父消息ID
            messageId = message.getParentMessageId();
        }

        // 构建上下文字符串
        StringBuilder context = new StringBuilder();
        context.append("以下是之前的对话历史：\n\n");

        // 计算当前上下文的token数
        int currentTokens = estimateTokenCount(context.toString());
        // 保留一些空间给提示词
        int reservedTokens = 100;

        // 从最早的消息开始添加，直到达到token限制
        for (ChatConversation.Message message : conversationHistory) {
            String role = message.getRole().equals("user") ? "用户" : "AI";
            String messageText = role + ": " + message.getContent() + "\n\n";

            context.append(messageText);
        }

        return context.toString();
    }

    protected ChatRequest createChatRequest(String prompt, boolean stream) {
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

    /**
     * 简单估算文本的token数量
     * 这是一个粗略的估计，实际token数可能因模型而异
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 粗略估计：英文大约每4个字符为1个token，中文大约每1.5个字符为1个token
        int englishChars = 0;
        int chineseChars = 0;

        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                chineseChars++;
            } else {
                englishChars++;
            }
        }

        return (int) (englishChars / 4.0 + chineseChars / 1.5);
    }
}
