package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 委托合同提示词策略
 */
@Component
public class CommissionContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是委托合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的委托合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 委托人和受托人信息\n");
        prompt.append("3. 委托事项的具体内容和范围\n");
        prompt.append("4. 委托期限\n");
        prompt.append("5. 委托权限的范围\n");
        prompt.append("6. 委托报酬及支付方式\n");
        prompt.append("7. 委托事项的完成标准\n");
        prompt.append("8. 委托费用的承担\n");
        prompt.append("9. 受托人的报告义务\n");
        prompt.append("10. 委托人的协助义务\n");
        prompt.append("11. 委托事项的转委托\n");
        prompt.append("12. 委托合同的变更和解除\n");
        prompt.append("13. 违约责任\n");
        prompt.append("14. 争议解决方式\n");
        prompt.append("15. 其他条款\n");
        prompt.append("\n请确保生成的委托合同符合《中华人民共和国民法典》第九百一十九条至第九百四十三条的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.COMMISSION_CONTRACT;
    }
}
