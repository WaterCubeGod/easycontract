package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 中介合同提示词策略
 */
@Component
public class IntermediaryContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是中介合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的中介合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 委托人和中介人信息\n");
        prompt.append("3. 中介服务的具体内容和范围\n");
        prompt.append("4. 中介服务期限\n");
        prompt.append("5. 中介报酬及支付条件\n");
        prompt.append("6. 中介费用的承担\n");
        prompt.append("7. 中介服务的完成标准\n");
        prompt.append("8. 中介人的告知义务\n");
        prompt.append("9. 中介人的保密义务\n");
        prompt.append("10. 委托人的协助义务\n");
        prompt.append("11. 中介合同的变更和解除\n");
        prompt.append("12. 违约责任\n");
        prompt.append("13. 争议解决方式\n");
        prompt.append("14. 其他条款\n");
        prompt.append("\n请确保生成的中介合同符合《中华人民共和国民法典》第九百六十条至第九百六十五条的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.INTERMEDIARY_CONTRACT;
    }
}
