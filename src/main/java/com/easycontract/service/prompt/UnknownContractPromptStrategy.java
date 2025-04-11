package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 无名合同提示词策略
 */
@Component
public class UnknownContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是无名合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的无名合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 签约方信息\n");
        prompt.append("3. 合同目的\n");
        prompt.append("4. 权利与义务\n");
        prompt.append("5. 合同期限\n");
        prompt.append("6. 违约责任\n");
        prompt.append("7. 争议解决方式\n");
        prompt.append("8. 其他条款\n");
        prompt.append("\n请确保生成的合同符合《中华人民共和国民法典》相关规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.UNKNOWN_CONTRACT;
    }
}
