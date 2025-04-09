package com.easycontract.service;

import com.easycontract.entity.ai.ChatResponse;
import com.easycontract.entity.es.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天历史服务接口
 */
public interface ChatHistoryService {
    
    /**
     * 保存聊天历史
     */
    ChatHistory saveChatHistory(Long userId, String username, String prompt, ChatResponse response);
    
    /**
     * 保存聊天历史（流式响应）
     */
    ChatHistory saveChatHistory(Long userId, String username, String prompt, String response, String model);
    
    /**
     * 根据ID查询聊天历史
     */
    ChatHistory findById(String id);
    
    /**
     * 根据用户ID查询聊天历史
     */
    List<ChatHistory> findByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询聊天历史
     */
    Page<ChatHistory> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查询聊天历史
     */
    List<ChatHistory> findByUserIdAndTimeRange(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据关键词搜索聊天历史
     */
    Page<ChatHistory> searchByKeyword(String keyword, Pageable pageable);
    
    /**
     * 根据用户ID和关键词搜索聊天历史
     */
    Page<ChatHistory> searchByUserIdAndKeyword(Long userId, String keyword, Pageable pageable);
    
    /**
     * 删除聊天历史
     */
    void deleteById(String id);
    
    /**
     * 删除用户的所有聊天历史
     */
    void deleteByUserId(Long userId);
}
