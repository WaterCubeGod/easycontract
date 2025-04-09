package com.easycontract.entity.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天历史记录实体类，用于存储到Elasticsearch
 */
@Data
@Document(indexName = "chat_history")
public class ChatHistory {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String prompt;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String response;

    @Field(type = FieldType.Keyword)
    private String model;

    @Field(type = FieldType.Integer)
    private Integer promptTokens;

    @Field(type = FieldType.Integer)
    private Integer responseTokens;

    @Field(type = FieldType.Integer)
    private Integer totalTokens;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    // 用于存储完整的对话上下文，包括多轮对话
    @Field(type = FieldType.Nested)
    private List<ChatMessage> messages;

    @Data
    public static class ChatMessage {
        @Field(type = FieldType.Keyword)
        private String role; // user, assistant, system

        @Field(type = FieldType.Text, analyzer = "ik_max_word")
        private String content;

        @Field(type = FieldType.Date)
        private LocalDateTime timestamp;
    }
}
