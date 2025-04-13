package com.easycontract.controller;

import com.easycontract.entity.es.ChatConversation;
import com.easycontract.entity.vo.MessageRequest;
import com.easycontract.entity.vo.PageResponse;
import com.easycontract.entity.vo.Response;
import com.easycontract.security.JwtUtils;
import com.easycontract.security.UserDetailsImpl;
import com.easycontract.security.UserDetailsServiceImpl;
import com.easycontract.service.AIService;
import com.easycontract.service.ChatConversationService;
import com.easycontract.service.PromptEngineeringService;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    @Autowired
    private ChatConversationService chatConversationService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

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

    /**
     * 从 JWT 令牌中获取用户
     */
    private UserDetailsImpl getCurrentUserFromJwt(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                try {
                    return (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        // 如果没有JWT令牌或令牌无效，尝试使用当前的SecurityContext
        return getCurrentUser();
    }



    // 仅管理员可以访问的测试接口
    @GetMapping("/admin/test")
    public Response<?> adminOnlyEndpoint() {
        return Response.success("管理员专属接口访问成功");
    }

    // 已将合同校验和生成功能集成到公共消息接口中，通过mode参数指定

    //---------------------- 树形对话结构相关接口 ----------------------
    // 所有对话都使用树形结构存储，支持分支和编辑功能

    /**
     * 创建新的对话
     */
    @PostMapping(value = "/conversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> createConversation(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody MessageRequest request) {
        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Flux.error(new RuntimeException("用户未登录或令牌无效"));
        }

        // 用于收集完整响应
        AtomicReference<StringBuilder> responseCollector = new AtomicReference<>(new StringBuilder());

        // 根据模式选择不同的响应生成方式
        Flux<String> responseFlux = aiService.responseText(request, null, null);

        return responseFlux
                .doOnNext(chunk -> {
                    // 收集响应内容
                    responseCollector.get().append(chunk);
                })
                .doOnComplete(() -> {
                    // 当流完成时，保存AI响应
                    String fullResponse = responseCollector.get().toString();
                    try {
                        // 创建新对话
                        ChatConversation conversation = chatConversationService.createConversation(
                                currentUser.getId(),
                                currentUser.getUsername(),
                                request.getContent()
                        );

                        String userMessageId = conversation.getMessages().get(0).getMessageId();

                        chatConversationService.addAssistantMessage(
                                conversation.getConversationId(),
                                fullResponse,
                                userMessageId,
                                "deepseek-chat", // 模型名称
                                0, // 由于流式输出无法获取token数，这里设为0
                                0
                        );
                    } catch (Exception e) {
                        System.err.println("保存流式响应失败: " + e.getMessage());
                    }
                });
    }

    /**
     * 在对话中添加用户消息并获取AI响应
     */
    @PostMapping(value = "/conversation/{conversationId}/message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> addMessageToConversation(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId,
            @RequestParam(required = false) String parentMessageId,
            @RequestBody MessageRequest request) {

        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Flux.error(new RuntimeException("用户未登录或令牌无效"));
        }

        ChatConversation conversation = chatConversationService.getConversation(conversationId);
        if (conversation == null) {
            return Flux.error(new RuntimeException("对话不存在"));
        }

        // 检查是否是当前用户的对话
        if (!conversation.getUserId().equals(currentUser.getId())) {
            return Flux.error(new RuntimeException("无权在此对话中添加消息"));
        }


        // 用于收集完整响应
        AtomicReference<StringBuilder> responseCollector = new AtomicReference<>(new StringBuilder());

        // 根据模式选择不同的响应生成方式
        Flux<String> responseFlux = aiService.responseText(request, conversationId, parentMessageId);

        return responseFlux
                .doOnNext(chunk -> {
                    // 收集响应内容
                    responseCollector.get().append(chunk);
                })
                .doOnComplete(() -> {
                    // 当流完成时，保存AI响应
                    String fullResponse = responseCollector.get().toString();
                    try {
                        // 获取最新添加的用户消息的ID
                        String lastMessageId = conversation.getMessages().stream()
                                .filter(m -> m.getRole().equals("user") &&
                                        (m.getParentMessageId() != null && m.getParentMessageId().equals(parentMessageId)))
                                .max((m1, m2) -> {
                                    // 安全地比较时间戳，处理可能的 null 值
                                    if (m1.getTimestamp() == null && m2.getTimestamp() == null) return 0;
                                    if (m1.getTimestamp() == null) return -1;
                                    if (m2.getTimestamp() == null) return 1;
                                    return m1.getTimestamp().compareTo(m2.getTimestamp());
                                })
                                .map(ChatConversation.Message::getMessageId)
                                .orElse(parentMessageId);

                        // 添加用户消息
                        chatConversationService.addUserMessage(conversationId, request.getContent(), parentMessageId);
                        // 添加AI响应
                        chatConversationService.addAssistantMessage(
                                conversationId,
                                fullResponse,
                                lastMessageId,
                                "deepseek-chat", // 模型名称
                                0, // 由于流式输出无法获取token数，这里设为0
                                0
                        );
                    } catch (Exception e) {
                        System.err.println("保存流式响应失败: " + e.getMessage());
                    }
                });

    }

    /**
     * 获取用户的所有对话
     */
    @GetMapping("/conversations")
    public Response<?> getUserConversations(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<ChatConversation> conversations = chatConversationService.getUserConversations(currentUser.getId(), pageRequest);

        // 使用自定义分页响应类
        PageResponse<ChatConversation> pageResponse = PageResponse.from(conversations);
        return Response.success(pageResponse);
    }

    /**
     * 获取对话详情
     */
    @GetMapping("/conversation/{conversationId}")
    public Response<?> getConversation(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId) {
        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
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
    public Response<?> getConversationTree(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId) {
        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
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
     * 编辑消息
     */
    @PutMapping("/conversation/{conversationId}/message/{messageId}")
    public Response<?> editMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @RequestParam(required = false) String reason,
            @RequestBody String newContent) {

        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
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
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId,
            @RequestParam String messageId,
            @RequestParam String branchName) {

        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
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
    public Response<?> deleteConversation(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId) {
        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
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
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String conversationId,
            @RequestParam String tag) {

        UserDetailsImpl currentUser = getCurrentUserFromJwt(authHeader);
        if (currentUser == null) {
            return Response.fail("用户未登录或令牌无效");
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
}
