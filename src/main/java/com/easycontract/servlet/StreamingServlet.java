package com.easycontract.servlet;

import com.easycontract.configuration.AIConfig;
import com.easycontract.entity.ai.ChatRequest;
import com.easycontract.entity.ai.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流式API Servlet
 * 直接处理流式API请求，完全绕过Spring Security
 */
@WebServlet("/api/direct/ai/stream")
@Component
public class StreamingServlet extends HttpServlet {

    private final AIConfig aiConfig;
    private final WebClient webClient;

    @Autowired
    public StreamingServlet(AIConfig aiConfig) {
        this.aiConfig = aiConfig;
        this.webClient = WebClient.builder()
                .baseUrl(aiConfig.getDeepseek().getApiUrl())
                .defaultHeader("Authorization", "Bearer " + aiConfig.getDeepseek().getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 读取请求体
        String prompt = request.getReader().lines().collect(Collectors.joining());
        
        // 设置响应头
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        
        // 创建请求对象
        ChatRequest chatRequest = createChatRequest(prompt);
        
        // 获取响应输出流
        PrintWriter writer = response.getWriter();
        
        // 调用AI API并直接写入响应
        Flux<String> responseFlux = webClient.post()
                .bodyValue(chatRequest)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> {
                    writer.write("data: " + chunk + "\n\n");
                    writer.flush();
                })
                .doOnError(error -> {
                    writer.write("data: 错误: " + error.getMessage() + "\n\n");
                    writer.flush();
                })
                .doOnComplete(() -> {
                    writer.close();
                });
        
        // 订阅流
        responseFlux.subscribe();
    }
    
    private ChatRequest createChatRequest(String prompt) {
        ChatRequest request = new ChatRequest();
        request.setModel(aiConfig.getDeepseek().getModel());
        request.setTemperature(aiConfig.getDeepseek().getTemperature());
        request.setMax_tokens(aiConfig.getDeepseek().getMaxTokens());
        request.setStream(true);

        List<Message> messages = new ArrayList<>();
        Message userMessage = new Message();
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        messages.add(userMessage);

        request.setMessages(messages);
        return request;
    }
}
