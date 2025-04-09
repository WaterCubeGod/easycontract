package com.easycontract.service.impl;

import com.easycontract.entity.es.ChatConversation;
import com.easycontract.repository.ChatConversationRepository;
import com.easycontract.service.AIService;
import com.easycontract.service.ChatConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatConversationServiceImpl implements ChatConversationService {

    @Autowired
    private ChatConversationRepository chatConversationRepository;
    
    @Autowired
    private AIService aiService;

    @Override
    public ChatConversation createConversation(Long userId, String username, String initialPrompt) {
        ChatConversation conversation = new ChatConversation();
        conversation.setConversationId(UUID.randomUUID().toString());
        conversation.setUserId(userId);
        conversation.setUsername(username);
        conversation.setTitle(generateTitle(initialPrompt));
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setStatus("active");
        conversation.setTags(new ArrayList<>());
        conversation.setMessages(new ArrayList<>());
        conversation.setContextPath("/");
        
        // 添加初始用户消息
        ChatConversation.Message message = new ChatConversation.Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setParentMessageId(null); // 根消息
        message.setRole("user");
        message.setContent(initialPrompt);
        message.setTimestamp(LocalDateTime.now());
        message.setEdited(false);
        message.setRevisions(new ArrayList<>());
        message.setBranches(new ArrayList<>());
        
        conversation.getMessages().add(message);
        
        // 初始化统计信息
        ChatConversation.Statistics stats = new ChatConversation.Statistics();
        stats.setMessageCount(1);
        stats.setUserMessageCount(1);
        stats.setAssistantMessageCount(0);
        stats.setTotalTokens(0);
        stats.setBranchCount(0);
        stats.setEditCount(0);
        conversation.setStatistics(stats);
        
        return chatConversationRepository.save(conversation);
    }

    @Override
    public ChatConversation addUserMessage(String conversationId, String content, String parentMessageId) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        // 创建新消息
        ChatConversation.Message message = new ChatConversation.Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setParentMessageId(parentMessageId);
        message.setRole("user");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setEdited(false);
        message.setRevisions(new ArrayList<>());
        message.setBranches(new ArrayList<>());
        
        // 添加消息
        conversation.getMessages().add(message);
        
        // 更新统计信息
        conversation.getStatistics().setMessageCount(conversation.getStatistics().getMessageCount() + 1);
        conversation.getStatistics().setUserMessageCount(conversation.getStatistics().getUserMessageCount() + 1);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        // 更新上下文路径
        updateContextPath(conversation);
        
        return chatConversationRepository.save(conversation);
    }

    @Override
    public ChatConversation addAssistantMessage(String conversationId, String content, String parentMessageId, 
                                              String model, Integer promptTokens, Integer responseTokens) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        // 创建新消息
        ChatConversation.Message message = new ChatConversation.Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setParentMessageId(parentMessageId);
        message.setRole("assistant");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setEdited(false);
        message.setModel(model);
        message.setPromptTokens(promptTokens);
        message.setResponseTokens(responseTokens);
        message.setTotalTokens(promptTokens + responseTokens);
        message.setRevisions(new ArrayList<>());
        message.setBranches(new ArrayList<>());
        
        // 添加消息
        conversation.getMessages().add(message);
        
        // 更新统计信息
        conversation.getStatistics().setMessageCount(conversation.getStatistics().getMessageCount() + 1);
        conversation.getStatistics().setAssistantMessageCount(conversation.getStatistics().getAssistantMessageCount() + 1);
        conversation.getStatistics().setTotalTokens(conversation.getStatistics().getTotalTokens() + message.getTotalTokens());
        conversation.setUpdatedAt(LocalDateTime.now());
        
        // 更新上下文路径
        updateContextPath(conversation);
        
        return chatConversationRepository.save(conversation);
    }

    @Override
    public ChatConversation editMessage(String conversationId, String messageId, String newContent, String reason) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        // 查找消息
        Optional<ChatConversation.Message> optMessage = conversation.getMessages().stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst();
        
        if (optMessage.isEmpty()) {
            throw new RuntimeException("消息不存在");
        }
        
        ChatConversation.Message message = optMessage.get();
        
        // 创建修订记录
        ChatConversation.Revision revision = new ChatConversation.Revision();
        revision.setRevisionId(UUID.randomUUID().toString());
        revision.setContent(message.getContent());
        revision.setTimestamp(LocalDateTime.now());
        revision.setReason(reason);
        
        if (message.getRevisions() == null) {
            message.setRevisions(new ArrayList<>());
        }
        message.getRevisions().add(revision);
        
        // 更新消息内容
        message.setContent(newContent);
        message.setEdited(true);
        
        // 更新统计信息
        conversation.getStatistics().setEditCount(conversation.getStatistics().getEditCount() + 1);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return chatConversationRepository.save(conversation);
    }

    @Override
    public ChatConversation createBranch(String conversationId, String messageId, String branchName) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        // 查找消息
        Optional<ChatConversation.Message> optMessage = conversation.getMessages().stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst();
        
        if (optMessage.isEmpty()) {
            throw new RuntimeException("消息不存在");
        }
        
        ChatConversation.Message message = optMessage.get();
        
        // 创建分支信息
        String branchId = UUID.randomUUID().toString();
        ChatConversation.Branch branch = new ChatConversation.Branch();
        branch.setBranchId(branchId);
        branch.setBranchName(branchName);
        branch.setCreatedAt(LocalDateTime.now());
        
        if (message.getBranches() == null) {
            message.setBranches(new ArrayList<>());
        }
        message.getBranches().add(branch);
        
        // 更新统计信息
        conversation.getStatistics().setBranchCount(conversation.getStatistics().getBranchCount() + 1);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return chatConversationRepository.save(conversation);
    }

    @Override
    public ChatConversation getConversation(String conversationId) {
        return chatConversationRepository.findById(conversationId).orElse(null);
    }

    @Override
    public Page<ChatConversation> getUserConversations(Long userId, Pageable pageable) {
        return chatConversationRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<ChatConversation> searchConversations(Long userId, String keyword, Pageable pageable) {
        return chatConversationRepository.findByUserIdAndTitleContaining(userId, keyword, pageable);
    }

    @Override
    public Page<ChatConversation> getConversationsByTag(Long userId, String tag, Pageable pageable) {
        return chatConversationRepository.findByUserIdAndTagsContaining(userId, tag, pageable);
    }

    @Override
    public Map<String, Object> getConversationTree(String conversationId) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        // 构建树形结构
        Map<String, Object> tree = new HashMap<>();
        tree.put("conversationId", conversation.getConversationId());
        tree.put("title", conversation.getTitle());
        tree.put("createdAt", conversation.getCreatedAt());
        
        // 构建消息树
        Map<String, List<Map<String, Object>>> messagesByParent = new HashMap<>();
        
        // 初始化根节点列表
        messagesByParent.put(null, new ArrayList<>());
        
        // 按父消息ID分组
        for (ChatConversation.Message message : conversation.getMessages()) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageId", message.getMessageId());
            messageMap.put("role", message.getRole());
            messageMap.put("content", message.getContent());
            messageMap.put("timestamp", message.getTimestamp());
            messageMap.put("edited", message.getEdited());
            
            if (message.getEdited() && message.getRevisions() != null && !message.getRevisions().isEmpty()) {
                List<Map<String, Object>> revisions = message.getRevisions().stream()
                        .map(r -> {
                            Map<String, Object> revMap = new HashMap<>();
                            revMap.put("revisionId", r.getRevisionId());
                            revMap.put("content", r.getContent());
                            revMap.put("timestamp", r.getTimestamp());
                            revMap.put("reason", r.getReason());
                            return revMap;
                        })
                        .collect(Collectors.toList());
                messageMap.put("revisions", revisions);
            }
            
            if (message.getBranches() != null && !message.getBranches().isEmpty()) {
                List<Map<String, Object>> branches = message.getBranches().stream()
                        .map(b -> {
                            Map<String, Object> branchMap = new HashMap<>();
                            branchMap.put("branchId", b.getBranchId());
                            branchMap.put("branchName", b.getBranchName());
                            branchMap.put("createdAt", b.getCreatedAt());
                            return branchMap;
                        })
                        .collect(Collectors.toList());
                messageMap.put("branches", branches);
            }
            
            // 确保父消息ID的列表已初始化
            messagesByParent.putIfAbsent(message.getParentMessageId(), new ArrayList<>());
            messagesByParent.get(message.getParentMessageId()).add(messageMap);
        }
        
        // 递归构建树
        List<Map<String, Object>> rootMessages = buildMessageTree(messagesByParent, null);
        tree.put("messages", rootMessages);
        
        return tree;
    }

    private List<Map<String, Object>> buildMessageTree(Map<String, List<Map<String, Object>>> messagesByParent, String parentId) {
        List<Map<String, Object>> messages = messagesByParent.getOrDefault(parentId, new ArrayList<>());
        
        for (Map<String, Object> message : messages) {
            String messageId = (String) message.get("messageId");
            List<Map<String, Object>> children = buildMessageTree(messagesByParent, messageId);
            if (!children.isEmpty()) {
                message.put("children", children);
            }
        }
        
        return messages;
    }

    @Override
    public ChatConversation archiveConversation(String conversationId) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        conversation.setStatus("archived");
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return chatConversationRepository.save(conversation);
    }

    @Override
    public void deleteConversation(String conversationId) {
        chatConversationRepository.deleteById(conversationId);
    }

    @Override
    public ChatConversation addTag(String conversationId, String tag) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        if (conversation.getTags() == null) {
            conversation.setTags(new ArrayList<>());
        }
        
        if (!conversation.getTags().contains(tag)) {
            conversation.getTags().add(tag);
            conversation.setUpdatedAt(LocalDateTime.now());
            return chatConversationRepository.save(conversation);
        }
        
        return conversation;
    }

    @Override
    public ChatConversation removeTag(String conversationId, String tag) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        if (conversation.getTags() != null && conversation.getTags().contains(tag)) {
            conversation.getTags().remove(tag);
            conversation.setUpdatedAt(LocalDateTime.now());
            return chatConversationRepository.save(conversation);
        }
        
        return conversation;
    }

    @Override
    public ChatConversation generateSummary(String conversationId) {
        ChatConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        // 提取对话内容
        StringBuilder dialogBuilder = new StringBuilder();
        for (ChatConversation.Message message : conversation.getMessages()) {
            dialogBuilder.append(message.getRole()).append(": ").append(message.getContent()).append("\n");
        }
        
        // 使用AI生成摘要
        String summary = "对话摘要"; // 这里应该调用AI服务生成摘要
        
        conversation.setSummary(summary);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return chatConversationRepository.save(conversation);
    }
    
    // 生成对话标题
    private String generateTitle(String initialPrompt) {
        // 简单实现：截取前20个字符作为标题
        if (initialPrompt == null || initialPrompt.isEmpty()) {
            return "新对话";
        }
        
        String title = initialPrompt.length() > 20 ? 
                initialPrompt.substring(0, 20) + "..." : 
                initialPrompt;
        
        return title;
    }
    
    // 更新上下文路径
    private void updateContextPath(ChatConversation conversation) {
        // 构建消息树
        Map<String, List<String>> childrenByParent = new HashMap<>();
        Map<String, ChatConversation.Message> messagesById = new HashMap<>();
        
        for (ChatConversation.Message message : conversation.getMessages()) {
            messagesById.put(message.getMessageId(), message);
            
            String parentId = message.getParentMessageId();
            childrenByParent.putIfAbsent(parentId, new ArrayList<>());
            childrenByParent.get(parentId).add(message.getMessageId());
        }
        
        // 找到根消息
        List<String> rootMessageIds = childrenByParent.getOrDefault(null, new ArrayList<>());
        if (rootMessageIds.isEmpty()) {
            conversation.setContextPath("/");
            return;
        }
        
        // 构建路径
        StringBuilder pathBuilder = new StringBuilder();
        for (String rootId : rootMessageIds) {
            pathBuilder.append("/").append(rootId);
            
            // 添加第一级子消息
            List<String> firstLevelChildren = childrenByParent.getOrDefault(rootId, new ArrayList<>());
            if (!firstLevelChildren.isEmpty()) {
                pathBuilder.append("/[");
                pathBuilder.append(String.join(",", firstLevelChildren));
                pathBuilder.append("]");
            }
        }
        
        conversation.setContextPath(pathBuilder.toString());
    }
}
