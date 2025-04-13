package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 通用对话提示词策略
 */
@Component
public class GeneralPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.GENERAL;
    }

    @Override
    public String contractRequest() {
        return "请回答用户的问题。";
    }
}
