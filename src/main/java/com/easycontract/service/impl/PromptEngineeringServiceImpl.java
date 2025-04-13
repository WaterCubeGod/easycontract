package com.easycontract.service.impl;

import com.easycontract.entity.enums.ContractEnum;
import com.easycontract.entity.vo.MessageRequest;
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
    public String generatePrompt(String context, MessageRequest request) {
        // 使用通用对话策略
        PromptStrategy strategy;
        switch (request.getMode()) {
            case "contract":
                // 使用合同生成策略，如果指定了合同类型则使用对应的策略
                strategy = promptStrategyFactory.getContractCreationStrategy(request.getContractEnum());
                break;
            case "check":
                strategy = promptStrategyFactory.getContractValidationStrategy();
                break;
            case "chat":
            default:
                strategy = promptStrategyFactory.getGeneralStrategy();
                break;
        }
        return strategy.generatePrompt(context);
    }
}
