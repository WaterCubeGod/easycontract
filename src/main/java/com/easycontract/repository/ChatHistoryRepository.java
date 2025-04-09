package com.easycontract.repository;

import com.easycontract.entity.es.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天历史存储库接口
 */
@Repository
public interface ChatHistoryRepository extends ElasticsearchRepository<ChatHistory, String> {
    
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
    List<ChatHistory> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据提示词内容模糊查询
     */
    Page<ChatHistory> findByPromptContaining(String keyword, Pageable pageable);
    
    /**
     * 根据用户ID和提示词内容模糊查询
     */
    Page<ChatHistory> findByUserIdAndPromptContaining(Long userId, String keyword, Pageable pageable);
}
