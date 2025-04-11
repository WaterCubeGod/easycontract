package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 保管合同提示词策略
 */
@Component
public class CustodialContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是保管合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的保管合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 保管人和寄存人信息\n");
        prompt.append("3. 保管物描述（包括名称、规格、数量、价值等）\n");
        prompt.append("4. 保管期限\n");
        prompt.append("5. 保管地点\n");
        prompt.append("6. 保管费用及支付方式\n");
        prompt.append("7. 保管物的交付和返还方式\n");
        prompt.append("8. 保管人的保管义务和注意义务\n");
        prompt.append("9. 保管物的使用限制\n");
        prompt.append("10. 保管物的损毁、灭失责任\n");
        prompt.append("11. 寄存人的告知义务\n");
        prompt.append("12. 违约责任\n");
        prompt.append("13. 合同解除条件\n");
        prompt.append("14. 争议解决方式\n");
        prompt.append("15. 其他条款\n");
        prompt.append("\n请确保生成的保管合同符合《中华人民共和国民法典》第八百八十八条至第九百零八条的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.CUSTODIAL_CONTRACT;
    }
}
