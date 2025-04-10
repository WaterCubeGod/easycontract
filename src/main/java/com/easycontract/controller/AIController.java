package com.easycontract.controller;

import com.easycontract.entity.ai.ChatResponse;
import com.easycontract.entity.es.ChatConversation;
import com.easycontract.entity.es.ChatHistory;
import com.easycontract.entity.vo.Response;
import com.easycontract.security.UserDetailsImpl;
import com.easycontract.service.AIService;
import com.easycontract.service.ChatConversationService;
import com.easycontract.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private ChatConversationService chatConversationService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * 获取当前登录用户
     */
    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        return null;
    }

    @PostMapping("/generate")
    public Response<?> generateText(@RequestBody String prompt) {
        try {
            // 获取当前用户
            UserDetailsImpl currentUser = getCurrentUser();

            // 调用AI生成文本
            ChatResponse result = aiService.generateText(prompt);

            // 如果用户已登录，则保存对话历史
            if (currentUser != null) {
                chatHistoryService.saveChatHistory(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    prompt,
                    result
                );
            }

            return Response.success(result);
        } catch (Exception e) {
            return Response.fail("AI生成失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateTextStream(@RequestBody String prompt) {
        UserDetailsImpl currentUser = getCurrentUser();

        // 用于收集完整响应
        AtomicReference<StringBuilder> responseCollector = new AtomicReference<>(new StringBuilder());

        return aiService.generateTextStream(prompt)
                .doOnNext(chunk -> {
                    // 收集响应内容
                    responseCollector.get().append(chunk);
                })
                .doOnComplete(() -> {
                    // 当流完成时，保存对话历史
                    try {
                        if (currentUser != null) {
                            String fullResponse = responseCollector.get().toString();
                            chatHistoryService.saveChatHistory(
                                currentUser.getId(),
                                currentUser.getUsername(),
                                prompt,
                                fullResponse,
                                "deepseek-chat" // 模型名称
                            );
                        }
                    } catch (Exception e) {
                        // 异常处理，仅记录日志，不影响流式输出
                        System.err.println("保存对话历史失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
    /**
     * 查询当前用户的对话历史
     */
    @GetMapping("/history")
    public Response<?> getUserChatHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatHistory> histories = chatHistoryService.findByUserId(currentUser.getId(), pageRequest);

        return Response.success(histories);
    }

    /**
     * 根据关键词搜索当前用户的对话历史
     */
    @GetMapping("/history/search")
    public Response<?> searchUserChatHistory(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatHistory> histories = chatHistoryService.searchByUserIdAndKeyword(
                currentUser.getId(), keyword, pageRequest);

        return Response.success(histories);
    }

    /**
     * 删除对话历史
     */
    @DeleteMapping("/history/{id}")
    public Response<?> deleteChatHistory(@PathVariable String id) {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatHistory history = chatHistoryService.findById(id);
        if (history == null) {
            return Response.fail("历史记录不存在");
        }

        // 检查是否是当前用户的历史记录或者管理员
        if (!history.getUserId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return Response.fail("无权删除此历史记录");
        }

        chatHistoryService.deleteById(id);
        return Response.success("删除成功");
    }

    /**
     * 管理员查询所有用户的对话历史
     */
    @GetMapping("/admin/history")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<?> getAllChatHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ChatHistory> histories;
        if (userId != null) {
            histories = chatHistoryService.findByUserId(userId, pageRequest);
        } else {
            // 这里需要实现一个查询所有历史的方法，这里简化为搜索空字符串
            histories = chatHistoryService.searchByKeyword("", pageRequest);
        }

        return Response.success(histories);
    }

    /**
     * 管理员删除用户的所有对话历史
     */
    @DeleteMapping("/admin/history/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<?> deleteUserChatHistory(@PathVariable Long userId) {
        chatHistoryService.deleteByUserId(userId);
        return Response.success("删除成功");
    }

    // 仅管理员可以访问的测试接口
    @GetMapping("/admin/test")
    public Response<?> adminOnlyEndpoint() {
        return Response.success("管理员专属接口访问成功");
    }

    //---------------------- 树形对话结构相关接口 ----------------------

    /**
     * 创建新的对话
     */
    @PostMapping("/conversation")
    public Response<?> createConversation(@RequestBody String initialPrompt) {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        try {
            ChatConversation conversation = chatConversationService.createConversation(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    initialPrompt
            );

            // 调用AI生成文本
            ChatResponse result = aiService.generateText(initialPrompt);

            // 添加AI响应
            if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
                String content = result.getChoices().get(0).getMessage().getContent();
                Integer promptTokens = result.getUsage() != null ? result.getUsage().getPrompt_tokens() : 0;
                Integer responseTokens = result.getUsage() != null ? result.getUsage().getCompletion_tokens() : 0;

                chatConversationService.addAssistantMessage(
                        conversation.getConversationId(),
                        content,
                        conversation.getMessages().get(0).getMessageId(),
                        result.getModel(),
                        promptTokens,
                        responseTokens
                );
            }

            return Response.success(chatConversationService.getConversation(conversation.getConversationId()));
        } catch (Exception e) {
            return Response.fail("创建对话失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的所有对话
     */
    @GetMapping("/conversations")
    public Response<?> getUserConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<ChatConversation> conversations = chatConversationService.getUserConversations(currentUser.getId(), pageRequest);

        return Response.success(conversations);
    }

    /**
     * 获取对话详情
     */
    @GetMapping("/conversation/{conversationId}")
    public Response<?> getConversation(@PathVariable String conversationId) {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话或者管理员
        if (!conversation.getUserId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return Response.fail("无权访问此对话");
        }

        return Response.success(conversation);
    }

    /**
     * 获取对话树形结构
     */
    @GetMapping("/conversation/{conversationId}/tree")
    public Response<?> getConversationTree(@PathVariable String conversationId) {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话或者管理员
        if (!conversation.getUserId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return Response.fail("无权访问此对话");
        }

        Map<String, Object> tree = chatConversationService.getConversationTree(conversationId);
        return Response.success(tree);
    }

    /**
     * 在对话中添加用户消息并获取AI响应
     */
    @PostMapping("/conversation/{conversationId}/message")
    public Response<?> addMessageToConversation(
            @PathVariable String conversationId,
            @RequestParam String parentMessageId,
            @RequestBody String content) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话
        if (!conversation.getUserId().equals(currentUser.getId())) {
            return Response.fail("无权在此对话中添加消息");
        }

        try {
            // 添加用户消息
            conversation = chatConversationService.addUserMessage(conversationId, content, parentMessageId);

            // 调用AI生成文本
            ChatResponse result = aiService.generateText(content);

            // 添加AI响应
            if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
                String aiContent = result.getChoices().get(0).getMessage().getContent();
                Integer promptTokens = result.getUsage() != null ? result.getUsage().getPrompt_tokens() : 0;
                Integer responseTokens = result.getUsage() != null ? result.getUsage().getCompletion_tokens() : 0;

                // 获取最新添加的用户消息的ID
                String lastMessageId = conversation.getMessages().stream()
                        .filter(m -> m.getRole().equals("user") && m.getParentMessageId().equals(parentMessageId))
                        .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                        .map(ChatConversation.Message::getMessageId)
                        .orElse(parentMessageId);

                conversation = chatConversationService.addAssistantMessage(
                        conversationId,
                        aiContent,
                        lastMessageId,
                        result.getModel(),
                        promptTokens,
                        responseTokens
                );
            }

            return Response.success(conversation);
        } catch (Exception e) {
            return Response.fail("添加消息失败: " + e.getMessage());
        }
    }

    /**
     * 编辑消息
     */
    @PutMapping("/conversation/{conversationId}/message/{messageId}")
    public Response<?> editMessage(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestParam(required = false) String reason,
            @RequestBody String newContent) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话
        if (!conversation.getUserId().equals(currentUser.getId())) {
            return Response.fail("无权编辑此对话中的消息");
        }

        try {
            conversation = chatConversationService.editMessage(conversationId, messageId, newContent, reason);
            return Response.success(conversation);
        } catch (Exception e) {
            return Response.fail("编辑消息失败: " + e.getMessage());
        }
    }

    /**
     * 创建分支
     */
    @PostMapping("/conversation/{conversationId}/branch")
    public Response<?> createBranch(
            @PathVariable String conversationId,
            @RequestParam String messageId,
            @RequestParam String branchName) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话
        if (!conversation.getUserId().equals(currentUser.getId())) {
            return Response.fail("无权在此对话中创建分支");
        }

        try {
            conversation = chatConversationService.createBranch(conversationId, messageId, branchName);
            return Response.success(conversation);
        } catch (Exception e) {
            return Response.fail("创建分支失败: " + e.getMessage());
        }
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversation/{conversationId}")
    public Response<?> deleteConversation(@PathVariable String conversationId) {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话或者管理员
        if (!conversation.getUserId().equals(currentUser.getId()) &&
                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return Response.fail("无权删除此对话");
        }

        try {
            chatConversationService.deleteConversation(conversationId);
            return Response.success("删除成功");
        } catch (Exception e) {
            return Response.fail("删除对话失败: " + e.getMessage());
        }
    }

    /**
     * 添加标签
     */
    @PostMapping("/conversation/{conversationId}/tag")
    public Response<?> addTag(
            @PathVariable String conversationId,
            @RequestParam String tag) {

        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Response.fail("对话不存在");
        }

        // 检查是否是当前用户的对话
        if (!conversation.getUserId().equals(currentUser.getId())) {
            return Response.fail("无权为此对话添加标签");
        }

        try {
            conversation = chatConversationService.addTag(conversationId, tag);
            return Response.success(conversation);
        } catch (Exception e) {
            return Response.fail("添加标签失败: " + e.getMessage());
        }
    }

    //---------------------- 树形对话结构相关接口 ----------------------

//    /**
//     * 创建新的对话
//     */
//    @PostMapping("/conversation")
//    public Response<?> createConversation(@RequestBody String initialPrompt) {
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        try {
//            ChatConversation conversation = chatConversationService.createConversation(
//                    currentUser.getId(),
//                    currentUser.getUsername(),
//                    initialPrompt
//            );
//
//            // 调用AI生成文本
//            ChatResponse result = aiService.generateText(initialPrompt);
//
//            // 添加AI响应
//            if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
//                String content = result.getChoices().get(0).getMessage().getContent();
//                Integer promptTokens = result.getUsage() != null ? result.getUsage().getPrompt_tokens() : 0;
//                Integer responseTokens = result.getUsage() != null ? result.getUsage().getCompletion_tokens() : 0;
//
//                chatConversationService.addAssistantMessage(
//                        conversation.getConversationId(),
//                        content,
//                        conversation.getMessages().get(0).getMessageId(),
//                        result.getModel(),
//                        promptTokens,
//                        responseTokens
//                );
//            }
//
//            return Response.success(chatConversationService.getConversation(conversation.getConversationId()));
//        } catch (Exception e) {
//            return Response.fail("创建对话失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 创建新的对话（流式响应）
//     */
//    @PostMapping(value = "/conversation/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> createConversationStream(@RequestBody String initialPrompt) {
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Flux.error(new RuntimeException("用户未登录"));
//        }
//
//        try {
//            // 创建新对话
//            ChatConversation conversation = chatConversationService.createConversation(
//                    currentUser.getId(),
//                    currentUser.getUsername(),
//                    initialPrompt
//            );
//
//            // 用于收集完整响应
//            AtomicReference<StringBuilder> responseCollector = new AtomicReference<>(new StringBuilder());
//            String userMessageId = conversation.getMessages().get(0).getMessageId();
//
//            // 调用AI生成流式文本
//            return aiService.generateTextStream(initialPrompt)
//                    .doOnNext(chunk -> {
//                        // 收集响应内容
//                        responseCollector.get().append(chunk);
//                    })
//                    .doOnComplete(() -> {
//                        // 当流完成时，保存AI响应
//                        String fullResponse = responseCollector.get().toString();
//                        try {
//                            chatConversationService.addAssistantMessage(
//                                    conversation.getConversationId(),
//                                    fullResponse,
//                                    userMessageId,
//                                    "deepseek-chat", // 模型名称
//                                    0, // 由于流式输出无法获取token数，这里设为0
//                                    0
//                            );
//                        } catch (Exception e) {
//                            System.err.println("保存流式响应失败: " + e.getMessage());
//                        }
//                    })
//                    // 将每个文本块包装为SSE格式
//                    .map(chunk -> "data: " + chunk + "\n\n");
//        } catch (Exception e) {
//            return Flux.error(new RuntimeException("创建对话失败: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * 获取用户的所有对话
//     */
//    @GetMapping("/conversations")
//    public Response<?> getUserConversations(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
//        Page<ChatConversation> conversations = chatConversationService.getUserConversations(currentUser.getId(), pageRequest);
//
//        return Response.success(conversations);
//    }
//
//    /**
//     * 获取对话详情
//     */
//    @GetMapping("/conversation/{conversationId}")
//    public Response<?> getConversation(@PathVariable String conversationId) {
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话或者管理员
//        if (!conversation.getUserId().equals(currentUser.getId()) &&
//                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
//            return Response.fail("无权访问此对话");
//        }
//
//        return Response.success(conversation);
//    }
//
//    /**
//     * 获取对话树形结构
//     */
//    @GetMapping("/conversation/{conversationId}/tree")
//    public Response<?> getConversationTree(@PathVariable String conversationId) {
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话或者管理员
//        if (!conversation.getUserId().equals(currentUser.getId()) &&
//                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
//            return Response.fail("无权访问此对话");
//        }
//
//        Map<String, Object> tree = chatConversationService.getConversationTree(conversationId);
//        return Response.success(tree);
//    }
//
//    /**
//     * 在对话中添加用户消息并获取AI响应
//     */
//    @PostMapping("/conversation/{conversationId}/message")
//    public Response<?> addMessageToConversation(
//            @PathVariable String conversationId,
//            @RequestParam String parentMessageId,
//            @RequestBody String content) {
//
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话
//        if (!conversation.getUserId().equals(currentUser.getId())) {
//            return Response.fail("无权在此对话中添加消息");
//        }
//
//        try {
//            // 添加用户消息
//            conversation = chatConversationService.addUserMessage(conversationId, content, parentMessageId);
//
//            // 调用AI生成文本
//            ChatResponse result = aiService.generateText(content);
//
//            // 添加AI响应
//            if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
//                String aiContent = result.getChoices().get(0).getMessage().getContent();
//                Integer promptTokens = result.getUsage() != null ? result.getUsage().getPrompt_tokens() : 0;
//                Integer responseTokens = result.getUsage() != null ? result.getUsage().getCompletion_tokens() : 0;
//
//                // 获取最新添加的用户消息的ID
//                String lastMessageId = conversation.getMessages().stream()
//                        .filter(m -> m.getRole().equals("user") && m.getParentMessageId().equals(parentMessageId))
//                        .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
//                        .map(ChatConversation.Message::getMessageId)
//                        .orElse(parentMessageId);
//
//                conversation = chatConversationService.addAssistantMessage(
//                        conversationId,
//                        aiContent,
//                        lastMessageId,
//                        result.getModel(),
//                        promptTokens,
//                        responseTokens
//                );
//            }
//
//            return Response.success(conversation);
//        } catch (Exception e) {
//            return Response.fail("添加消息失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 在对话中添加用户消息并获取AI流式响应
//     */
//    @PostMapping(value = "/conversation/{conversationId}/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> addMessageToConversationStream(
//            @PathVariable String conversationId,
//            @RequestParam String parentMessageId,
//            @RequestBody String content) {
//
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Flux.error(new RuntimeException("用户未登录"));
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Flux.error(new RuntimeException("对话不存在"));
//        }
//
//        // 检查是否是当前用户的对话
//        if (!conversation.getUserId().equals(currentUser.getId())) {
//            return Flux.error(new RuntimeException("无权在此对话中添加消息"));
//        }
//
//        try {
//            // 添加用户消息
//            conversation = chatConversationService.addUserMessage(conversationId, content, parentMessageId);
//
//            // 获取最新添加的用户消息的ID
//            String lastMessageId = conversation.getMessages().stream()
//                    .filter(m -> m.getRole().equals("user") && m.getParentMessageId().equals(parentMessageId))
//                    .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
//                    .map(ChatConversation.Message::getMessageId)
//                    .orElse(parentMessageId);
//
//            // 用于收集完整响应
//            AtomicReference<StringBuilder> responseCollector = new AtomicReference<>(new StringBuilder());
//
//            // 调用AI生成流式文本
//            return aiService.generateTextStream(content)
//                    .doOnNext(chunk -> {
//                        // 收集响应内容
//                        responseCollector.get().append(chunk);
//                    })
//                    .doOnComplete(() -> {
//                        // 当流完成时，保存AI响应
//                        String fullResponse = responseCollector.get().toString();
//                        try {
//                            chatConversationService.addAssistantMessage(
//                                    conversationId,
//                                    fullResponse,
//                                    lastMessageId,
//                                    "deepseek-chat", // 模型名称
//                                    0, // 由于流式输出无法获取token数，这里设为0
//                                    0
//                            );
//                        } catch (Exception e) {
//                            System.err.println("保存流式响应失败: " + e.getMessage());
//                        }
//                    })
//                    // 将每个文本块包装为SSE格式
//                    .map(chunk -> "data: " + chunk + "\n\n");
//        } catch (Exception e) {
//            return Flux.error(new RuntimeException("添加消息失败: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * 编辑消息
//     */
//    @PutMapping("/conversation/{conversationId}/message/{messageId}")
//    public Response<?> editMessage(
//            @PathVariable String conversationId,
//            @PathVariable String messageId,
//            @RequestParam(required = false) String reason,
//            @RequestBody String newContent) {
//
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话
//        if (!conversation.getUserId().equals(currentUser.getId())) {
//            return Response.fail("无权编辑此对话中的消息");
//        }
//
//        try {
//            conversation = chatConversationService.editMessage(conversationId, messageId, newContent, reason);
//            return Response.success(conversation);
//        } catch (Exception e) {
//            return Response.fail("编辑消息失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 创建分支
//     */
//    @PostMapping("/conversation/{conversationId}/branch")
//    public Response<?> createBranch(
//            @PathVariable String conversationId,
//            @RequestParam String messageId,
//            @RequestParam String branchName) {
//
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话
//        if (!conversation.getUserId().equals(currentUser.getId())) {
//            return Response.fail("无权在此对话中创建分支");
//        }
//
//        try {
//            conversation = chatConversationService.createBranch(conversationId, messageId, branchName);
//            return Response.success(conversation);
//        } catch (Exception e) {
//            return Response.fail("创建分支失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 删除对话
//     */
//    @DeleteMapping("/conversation/{conversationId}")
//    public Response<?> deleteConversation(@PathVariable String conversationId) {
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话或者管理员
//        if (!conversation.getUserId().equals(currentUser.getId()) &&
//                !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
//            return Response.fail("无权删除此对话");
//        }
//
//        try {
//            chatConversationService.deleteConversation(conversationId);
//            return Response.success("删除成功");
//        } catch (Exception e) {
//            return Response.fail("删除对话失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 添加标签
//     */
//    @PostMapping("/conversation/{conversationId}/tag")
//    public Response<?> addTag(
//            @PathVariable String conversationId,
//            @RequestParam String tag) {
//
//        UserDetailsImpl currentUser = getCurrentUser();
//        if (currentUser == null) {
//            return Response.fail("用户未登录");
//        }
//
//        ChatConversation conversation = chatConversationService.getConversation(conversationId);
//        if (conversation == null) {
//            return Response.fail("对话不存在");
//        }
//
//        // 检查是否是当前用户的对话
//        if (!conversation.getUserId().equals(currentUser.getId())) {
//            return Response.fail("无权为此对话添加标签");
//        }
//
//        try {
//            conversation = chatConversationService.addTag(conversationId, tag);
//            return Response.success(conversation);
//        } catch (Exception e) {
//            return Response.fail("添加标签失败: " + e.getMessage());
//        }
//    }
}
