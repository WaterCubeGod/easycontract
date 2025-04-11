package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 通用对话提示词策略
 */
@Component
public class GeneralPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        return context + "\n\n请回答用户的问题。";
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.GENERAL;
    }
}
