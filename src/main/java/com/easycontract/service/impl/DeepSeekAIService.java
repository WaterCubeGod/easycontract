package com.easycontract.service.impl;

import com.easycontract.configuration.AIConfig;
import com.easycontract.entity.ai.*;
import com.easycontract.service.AIService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easycontract.entity.es.ChatConversation;

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

    @Override
    public String buildConversationContext(ChatConversation conversation, String currentMessageId) {
        // 使用配置的最大上下文长度
        int maxContextTokens = aiConfig.getDeepseek().getMaxContextTokens();
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
        // 保留一些空间给最后的提示语
        int reservedTokens = 100;

        // 从最早的消息开始添加，直到达到token限制
        for (ChatConversation.Message message : conversationHistory) {
            String role = message.getRole().equals("user") ? "用户" : "AI";
            String messageText = role + ": " + message.getContent() + "\n\n";
            int messageTokens = estimateTokenCount(messageText);

            // 检查是否超出限制
            if (currentTokens + messageTokens + reservedTokens > maxContextTokens) {
                // 如果超出限制，停止添加更多消息
                context.append("注意：由于对话历史过长，只显示了最近的部分对话。\n\n");
                break;
            }

            context.append(messageText);
            currentTokens += messageTokens;
        }

        // 添加当前用户的问题
        context.append("请基于以上对话历史回答用户的问题。");

        return context.toString();
    }
}
