package com.easycontract.service;

import com.easycontract.entity.es.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Map;

public interface ChatConversationService {

    // 创建新对话
    ChatConversation createConversation(Long userId, String username, String initialPrompt);

    // 添加用户消息
    ChatConversation addUserMessage(String conversationId, String content, String parentMessageId);

    // 添加AI响应
    ChatConversation addAssistantMessage(String conversationId, String content, String parentMessageId,
                                        String model, Integer promptTokens, Integer responseTokens);

    // 编辑消息
    ChatConversation editMessage(String conversationId, String messageId, String newContent, String reason);

    // 创建分支
    ChatConversation createBranch(String conversationId, String messageId, String branchName);

    // 获取对话
    ChatConversation getConversation(String conversationId);

    // 获取用户的所有对话
    Page<ChatConversation> getUserConversations(Long userId, Pageable pageable);

    // 搜索对话
    Page<ChatConversation> searchConversations(Long userId, String keyword, Pageable pageable);

    // 按标签过滤对话
    Page<ChatConversation> getConversationsByTag(Long userId, String tag, Pageable pageable);

    // 获取对话树
    Map<String, Object> getConversationTree(String conversationId);

    // 归档对话
    ChatConversation archiveConversation(String conversationId);

    // 删除对话
    void deleteConversation(String conversationId);

    // 添加标签
    ChatConversation addTag(String conversationId, String tag);

    // 移除标签
    ChatConversation removeTag(String conversationId, String tag);

    // 生成对话摘要
    ChatConversation generateSummary(String conversationId);
}
