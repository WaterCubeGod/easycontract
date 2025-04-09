package com.easycontract.entity.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天对话实体类，用于存储到Elasticsearch
 * 支持树形结构、修改痕迹追踪和分支管理
 */
@Data
@Document(indexName = "chat_conversation")
public class ChatConversation {
    
    @Id
    private String conversationId;
    
    @Field(type = FieldType.Text)
    private String title;
    
    @Field(type = FieldType.Long)
    private Long userId;
    
    @Field(type = FieldType.Keyword)
    private String username;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
    
    @Field(type = FieldType.Keyword)
    private String status; // active, archived, deleted
    
    @Field(type = FieldType.Keyword)
    private List<String> tags;
    
    @Field(type = FieldType.Nested)
    private List<Message> messages;
    
    @Field(type = FieldType.Text)
    private String summary;
    
    @Field(type = FieldType.Keyword)
    private String contextPath;
    
    @Field(type = FieldType.Object)
    private Statistics statistics;
    
    /**
     * 消息类，包含消息内容、修订历史和分支信息
     */
    @Data
    public static class Message {
        @Field(type = FieldType.Keyword)
        private String messageId;
        
        @Field(type = FieldType.Keyword)
        private String parentMessageId;
        
        @Field(type = FieldType.Keyword)
        private String role; // user, assistant, system
        
        @Field(type = FieldType.Text)
        private String content;
        
        @Field(type = FieldType.Date)
        private LocalDateTime timestamp;
        
        @Field(type = FieldType.Boolean)
        private Boolean edited;
        
        @Field(type = FieldType.Keyword)
        private String model;
        
        @Field(type = FieldType.Integer)
        private Integer promptTokens;
        
        @Field(type = FieldType.Integer)
        private Integer responseTokens;
        
        @Field(type = FieldType.Integer)
        private Integer totalTokens;
        
        @Field(type = FieldType.Object)
        private Map<String, Object> metadata;
        
        @Field(type = FieldType.Nested)
        private List<Revision> revisions;
        
        @Field(type = FieldType.Nested)
        private List<Branch> branches;
    }
    
    /**
     * 修订历史类，记录消息的修改历史
     */
    @Data
    public static class Revision {
        @Field(type = FieldType.Keyword)
        private String revisionId;
        
        @Field(type = FieldType.Text)
        private String content;
        
        @Field(type = FieldType.Date)
        private LocalDateTime timestamp;
        
        @Field(type = FieldType.Text)
        private String reason;
    }
    
    /**
     * 分支类，记录从某条消息派生的分支信息
     */
    @Data
    public static class Branch {
        @Field(type = FieldType.Keyword)
        private String branchId;
        
        @Field(type = FieldType.Text)
        private String branchName;
        
        @Field(type = FieldType.Date)
        private LocalDateTime createdAt;
    }
    
    /**
     * 统计信息类，记录对话的统计数据
     */
    @Data
    public static class Statistics {
        @Field(type = FieldType.Integer)
        private Integer messageCount;
        
        @Field(type = FieldType.Integer)
        private Integer userMessageCount;
        
        @Field(type = FieldType.Integer)
        private Integer assistantMessageCount;
        
        @Field(type = FieldType.Integer)
        private Integer totalTokens;
        
        @Field(type = FieldType.Integer)
        private Integer branchCount;
        
        @Field(type = FieldType.Integer)
        private Integer editCount;
    }
}
