package com.easycontract.service.impl;

import com.easycontract.entity.ai.ChatResponse;
import com.easycontract.entity.es.ChatHistory;
import com.easycontract.repository.ChatHistoryRepository;
import com.easycontract.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 聊天历史服务实现类
 */
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Override
    public ChatHistory saveChatHistory(Long userId, String username, String prompt, ChatResponse response) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(UUID.randomUUID().toString());
        chatHistory.setUserId(userId);
        chatHistory.setUsername(username);
        chatHistory.setPrompt(prompt);
        
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            chatHistory.setResponse(response.getChoices().get(0).getMessage().getContent());
        }
        
        chatHistory.setModel(response != null ? response.getModel() : "unknown");
        
        if (response != null && response.getUsage() != null) {
            chatHistory.setPromptTokens(response.getUsage().getPrompt_tokens());
            chatHistory.setResponseTokens(response.getUsage().getCompletion_tokens());
            chatHistory.setTotalTokens(response.getUsage().getTotal_tokens());
        }
        
        chatHistory.setCreatedAt(LocalDateTime.now());
        
        // 设置消息历史
        List<ChatHistory.ChatMessage> messages = new ArrayList<>();
        
        // 用户消息
        ChatHistory.ChatMessage userMessage = new ChatHistory.ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        userMessage.setTimestamp(LocalDateTime.now());
        messages.add(userMessage);
        
        // AI响应消息
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            ChatHistory.ChatMessage aiMessage = new ChatHistory.ChatMessage();
            aiMessage.setRole("assistant");
            aiMessage.setContent(response.getChoices().get(0).getMessage().getContent());
            aiMessage.setTimestamp(LocalDateTime.now());
            messages.add(aiMessage);
        }
        
        chatHistory.setMessages(messages);
        
        return chatHistoryRepository.save(chatHistory);
    }

    @Override
    public ChatHistory saveChatHistory(Long userId, String username, String prompt, String response, String model) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(UUID.randomUUID().toString());
        chatHistory.setUserId(userId);
        chatHistory.setUsername(username);
        chatHistory.setPrompt(prompt);
        chatHistory.setResponse(response);
        chatHistory.setModel(model);
        chatHistory.setCreatedAt(LocalDateTime.now());
        
        // 设置消息历史
        List<ChatHistory.ChatMessage> messages = new ArrayList<>();
        
        // 用户消息
        ChatHistory.ChatMessage userMessage = new ChatHistory.ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        userMessage.setTimestamp(LocalDateTime.now());
        messages.add(userMessage);
        
        // AI响应消息
        ChatHistory.ChatMessage aiMessage = new ChatHistory.ChatMessage();
        aiMessage.setRole("assistant");
        aiMessage.setContent(response);
        aiMessage.setTimestamp(LocalDateTime.now());
        messages.add(aiMessage);
        
        chatHistory.setMessages(messages);
        
        return chatHistoryRepository.save(chatHistory);
    }

    @Override
    public ChatHistory findById(String id) {
        return chatHistoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<ChatHistory> findByUserId(Long userId) {
        return chatHistoryRepository.findByUserId(userId);
    }

    @Override
    public Page<ChatHistory> findByUserId(Long userId, Pageable pageable) {
        return chatHistoryRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<ChatHistory> findByUserIdAndTimeRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return chatHistoryRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }

    @Override
    public Page<ChatHistory> searchByKeyword(String keyword, Pageable pageable) {
        return chatHistoryRepository.findByPromptContaining(keyword, pageable);
    }

    @Override
    public Page<ChatHistory> searchByUserIdAndKeyword(Long userId, String keyword, Pageable pageable) {
        return chatHistoryRepository.findByUserIdAndPromptContaining(userId, keyword, pageable);
    }

    @Override
    public void deleteById(String id) {
        chatHistoryRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        List<ChatHistory> histories = chatHistoryRepository.findByUserId(userId);
        chatHistoryRepository.deleteAll(histories);
    }
}
