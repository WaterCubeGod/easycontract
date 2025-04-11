package com.easycontract.service.impl;

import com.easycontract.entity.enums.ContractEnum;
import com.easycontract.service.PromptEngineeringService;
import com.easycontract.service.prompt.PromptStrategy;
import com.easycontract.service.prompt.PromptStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 提示词工程服务实现类
 */
@Service
public class PromptEngineeringServiceImpl implements PromptEngineeringService {

    @Autowired
    private PromptStrategyFactory promptStrategyFactory;

    @Override
    public String generateGeneralPrompt(String context) {
        // 使用通用对话策略
        PromptStrategy strategy = promptStrategyFactory.getGeneralStrategy();
        return strategy.generatePrompt(context, null);
    }

    @Override
    public String generateContractValidationPrompt(String context, String contractContent) {
        // 使用合同校验策略
        PromptStrategy strategy = promptStrategyFactory.getContractValidationStrategy();
        return strategy.generatePrompt(context, contractContent);
    }

    @Override
    public String generateContractCreationPrompt(String context, String requirements) {
        // 使用通用合同生成策略
        PromptStrategy strategy = promptStrategyFactory.getContractCreationStrategy(ContractEnum.NULL_CONTRACT);
        return strategy.generatePrompt(context, requirements);
    }

    @Override
    public String generateContractPrompt(String context, String requirements, ContractEnum contractType) {
        // 如果合同类型为 null，则使用 NULL_CONTRACT
        if (contractType == null) {
            contractType = ContractEnum.NULL_CONTRACT;
        }

        // 根据合同类型选择对应的策略
        PromptStrategy strategy = promptStrategyFactory.getContractCreationStrategy(contractType);
        return strategy.generatePrompt(context, requirements);
    }
}
