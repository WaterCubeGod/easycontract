package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 合同校验提示词策略
 */
@Component
public class ContractValidationPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是需要校验的合同内容：\n");
        prompt.append(content);
        prompt.append("\n\n请对上述合同内容进行全面审核，检查以下方面：\n");
        prompt.append("1. 法律合规性：检查合同条款是否符合相关法律法规\n");
        prompt.append("2. 权责明确性：检查权利义务是否明确，责任划分是否清晰\n");
        prompt.append("3. 逻辑一致性：检查合同内部逻辑是否一致，有无矛盾之处\n");
        prompt.append("4. 语言规范性：检查语言表述是否准确、清晰、无歧义\n");
        prompt.append("5. 风险点识别：识别合同中可能存在的风险点和隐患\n");
        prompt.append("\n请提供详细的审核报告，包括问题描述、风险等级和修改建议。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.CONTRACT_VALIDATION;
    }
}
