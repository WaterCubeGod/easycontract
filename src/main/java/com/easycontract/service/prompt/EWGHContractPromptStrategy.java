package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 供用电、水、气、热力合同提示词策略
 */
@Component
public class EWGHContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是供用电、水、气、热力合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的供用电、水、气、热力合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 供应方和用户方信息\n");
        prompt.append("3. 供应内容（电、水、气或热力）\n");
        prompt.append("4. 供应地点和用途\n");
        prompt.append("5. 计量方式和标准\n");
        prompt.append("6. 价格和费用计算方式\n");
        prompt.append("7. 结算方式和期限\n");
        prompt.append("8. 供应方和用户方的权利义务\n");
        prompt.append("9. 服务质量和安全标准\n");
        prompt.append("10. 违约责任\n");
        prompt.append("11. 合同期限和终止条件\n");
        prompt.append("12. 争议解决方式\n");
        prompt.append("13. 其他条款\n");
        prompt.append("\n请确保生成的合同符合《中华人民共和国民法典》和相关行业法规的规定，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.EWGH_CONTRACT;
    }
}
