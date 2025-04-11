package com.easycontract.service;

import com.easycontract.entity.enums.ContractEnum;
import com.easycontract.entity.es.ChatConversation;
import reactor.core.publisher.Flux;

public interface AIService {
    /**
     * 生成文本（流式响应）
     * @param prompt 提示词
     * @return 文本流
     */
    Flux<String> generateText(String prompt);

    /**
     * 构建对话上下文
     * @param conversation 对话对象
     * @param currentMessageId 当前消息ID
     * @return 构建的上下文字符串（不包含提示词）
     */
    String buildConversationContext(ChatConversation conversation, String currentMessageId);

    /**
     * 使用通用提示词生成文本
     * @param conversation 对话对象
     * @param currentMessageId 当前消息ID
     * @return 文本流
     */
    Flux<String> generateGeneralResponse(ChatConversation conversation, String currentMessageId);

    /**
     * 使用合同校验提示词生成文本
     * @param conversation 对话对象
     * @param currentMessageId 当前消息ID
     * @param contractContent 合同内容
     * @return 文本流
     */
    Flux<String> generateContractValidation(ChatConversation conversation, String currentMessageId, String contractContent);

    /**
     * 使用合同生成提示词生成文本
     * @param conversation 对话对象
     * @param currentMessageId 当前消息ID
     * @param requirements 合同需求
     * @param contractType 合同类型，如果为 null 则使用 NULL_CONTRACT
     * @return 文本流
     */
    Flux<String> generateContractCreation(ChatConversation conversation, String currentMessageId,
                                        String requirements, ContractEnum contractType);
}
