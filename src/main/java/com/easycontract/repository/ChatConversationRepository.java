package com.easycontract.repository;

import com.easycontract.entity.es.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天对话存储库接口
 */
@Repository
public interface ChatConversationRepository extends ElasticsearchRepository<ChatConversation, String> {
    
    /**
     * 根据用户ID查询对话
     */
    List<ChatConversation> findByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询对话
     */
    Page<ChatConversation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态分页查询对话
     */
    Page<ChatConversation> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * 根据用户ID和标签分页查询对话
     */
    Page<ChatConversation> findByUserIdAndTagsContaining(Long userId, String tag, Pageable pageable);
    
    /**
     * 根据用户ID和标题关键词分页查询对话
     */
    Page<ChatConversation> findByUserIdAndTitleContaining(Long userId, String keyword, Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查询对话
     */
    List<ChatConversation> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
