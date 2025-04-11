package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 买卖合同提示词策略
 */
@Component
public class SalesContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是买卖合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的买卖合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 买卖双方信息\n");
        prompt.append("3. 标的物描述（包括名称、规格、数量、质量要求等）\n");
        prompt.append("4. 价款与支付方式\n");
        prompt.append("5. 交付方式、时间和地点\n");
        prompt.append("6. 检验标准与方法\n");
        prompt.append("7. 所有权转移\n");
        prompt.append("8. 风险责任\n");
        prompt.append("9. 违约责任\n");
        prompt.append("10. 争议解决方式\n");
        prompt.append("11. 其他条款\n");
        prompt.append("\n请确保生成的买卖合同符合《中华人民共和国民法典》相关规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.SALES_CONTRACT;
    }
}
