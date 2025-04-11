package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 仓储合同提示词策略
 */
@Component
public class WarehousingContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是仓储合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的仓储合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 仓储人和存货人信息\n");
        prompt.append("3. 仓储物描述（包括名称、规格、数量、价值等）\n");
        prompt.append("4. 仓储期限\n");
        prompt.append("5. 仓储地点和仓储条件\n");
        prompt.append("6. 仓储费用及支付方式\n");
        prompt.append("7. 仓储物的交付和提取方式\n");
        prompt.append("8. 仓单的签发和流转\n");
        prompt.append("9. 仓储人的保管义务\n");
        prompt.append("10. 仓储物的检验和养护\n");
        prompt.append("11. 仓储物的损毁、灭失责任\n");
        prompt.append("12. 存货人的告知义务\n");
        prompt.append("13. 违约责任\n");
        prompt.append("14. 合同解除条件\n");
        prompt.append("15. 争议解决方式\n");
        prompt.append("16. 其他条款\n");
        prompt.append("\n请确保生成的仓储合同符合《中华人民共和国民法典》第九百零九条至第九百二十五条的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.WAREHOUSING_CONTRACT;
    }
}
