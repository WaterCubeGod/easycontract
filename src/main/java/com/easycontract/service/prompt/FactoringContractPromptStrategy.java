package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 保理合同提示词策略
 */
@Component
public class FactoringContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是保理合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的保理合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 保理商和债权人信息\n");
        prompt.append("3. 基础交易合同描述\n");
        prompt.append("4. 应收账款的转让范围和方式\n");
        prompt.append("5. 保理融资金额和条件\n");
        prompt.append("6. 保理费用和利息\n");
        prompt.append("7. 还款方式和期限\n");
        prompt.append("8. 应收账款管理和催收\n");
        prompt.append("9. 债权人的陈述与保证\n");
        prompt.append("10. 通知义务\n");
        prompt.append("11. 违约责任\n");
        prompt.append("12. 合同解除条件\n");
        prompt.append("13. 争议解决方式\n");
        prompt.append("14. 其他条款\n");
        prompt.append("\n请确保生成的保理合同符合《中华人民共和国民法典》第七百六十一条至第七百六十九条的规定，以及相关金融法规的要求，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.FACTORING_CONTRACT;
    }
}
