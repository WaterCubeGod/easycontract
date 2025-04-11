package com.easycontract.service.prompt;

/**
 * 提示词策略接口
 * 使用策略模式处理不同类型的提示词生成
 */
public interface PromptStrategy {
    
    /**
     * 生成提示词
     * @param context 上下文
     * @param content 内容
     * @return 生成的提示词
     */
    String generatePrompt(String context, String content);
    
    /**
     * 获取策略类型
     * @return 策略类型
     */
    PromptStrategyType getType();
}
