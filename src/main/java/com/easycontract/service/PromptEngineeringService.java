package com.easycontract.service;

import com.easycontract.entity.enums.ContractEnum;
import com.easycontract.entity.vo.MessageRequest;

/**
 * 提示词工程服务接口
 * 负责根据不同场景生成不同的提示词
 */
public interface PromptEngineeringService {

    /**
     * 生成对话提示词
     * @param context 对话上下文
     * @return 完整的提示词
     */
    String generatePrompt(String context, MessageRequest request);
}
