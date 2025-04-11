package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 借款合同提示词策略
 */
@Component
public class LoanContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是借款合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的借款合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 借款人和贷款人信息\n");
        prompt.append("3. 借款金额（包括大小写金额）\n");
        prompt.append("4. 借款期限\n");
        prompt.append("5. 借款用途\n");
        prompt.append("6. 利率与计息方式\n");
        prompt.append("7. 还款方式与还款计划\n");
        prompt.append("8. 担保方式（如有）\n");
        prompt.append("9. 违约责任\n");
        prompt.append("10. 争议解决方式\n");
        prompt.append("11. 其他条款\n");
        prompt.append("\n请确保生成的借款合同符合《中华人民共和国民法典》和相关金融法规的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.LOAN_CONTRACT;
    }
}
