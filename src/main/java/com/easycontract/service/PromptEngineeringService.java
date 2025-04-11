package com.easycontract.service;

import com.easycontract.entity.enums.ContractEnum;

/**
 * 提示词工程服务接口
 * 负责根据不同场景生成不同的提示词
 */
public interface PromptEngineeringService {

    /**
     * 生成通用对话提示词
     * @param context 对话上下文
     * @return 完整的提示词
     */
    String generateGeneralPrompt(String context);

    /**
     * 生成合同校验提示词
     * @param context 对话上下文
     * @param contractContent 合同内容
     * @return 完整的提示词
     */
    String generateContractValidationPrompt(String context, String contractContent);

    /**
     * 生成合同生成提示词
     * @param context 对话上下文
     * @param requirements 合同需求
     * @return 完整的提示词
     */
    String generateContractCreationPrompt(String context, String requirements);

    /**
     * 根据合同类型生成合同提示词
     * @param context 对话上下文
     * @param requirements 合同需求
     * @param contractType 合同类型
     * @return 完整的提示词
     */
    String generateContractPrompt(String context, String requirements, ContractEnum contractType);
}
