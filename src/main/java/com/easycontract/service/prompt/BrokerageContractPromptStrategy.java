package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 行纪合同提示词策略
 */
@Component
public class BrokerageContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是行纪合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的行纪合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 委托人和行纪人信息\n");
        prompt.append("3. 行纪事项的具体内容和范围\n");
        prompt.append("4. 行纪权限的范围\n");
        prompt.append("5. 行纪期限\n");
        prompt.append("6. 行纪报酬及支付方式\n");
        prompt.append("7. 行纪费用的承担\n");
        prompt.append("8. 交易的价格区间或定价原则\n");
        prompt.append("9. 行纪人的报告义务\n");
        prompt.append("10. 委托人的协助义务\n");
        prompt.append("11. 行纪人的自己成为对方当事人的条件\n");
        prompt.append("12. 行纪合同的变更和解除\n");
        prompt.append("13. 违约责任\n");
        prompt.append("14. 争议解决方式\n");
        prompt.append("15. 其他条款\n");
        prompt.append("\n请确保生成的行纪合同符合《中华人民共和国民法典》第九百五十一条至第九百五十九条的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.BROKERAGE_CONTRACT;
    }
}
