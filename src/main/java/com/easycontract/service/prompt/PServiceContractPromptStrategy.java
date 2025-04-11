package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 物业服务合同提示词策略
 */
@Component
public class PServiceContractPromptStrategy implements PromptStrategy {
    
    @Override
    public String generatePrompt(String context, String content) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是物业服务合同生成需求：\n");
        prompt.append(content);
        prompt.append("\n\n请根据上述需求生成一份完整的物业服务合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 物业服务企业和业主（或业主委员会）信息\n");
        prompt.append("3. 服务区域和项目\n");
        prompt.append("4. 服务内容和标准\n");
        prompt.append("5. 服务期限\n");
        prompt.append("6. 物业服务费用及其调整机制\n");
        prompt.append("7. 费用的支付方式和期限\n");
        prompt.append("8. 物业共用部位和共用设施设备的使用、维护和管理\n");
        prompt.append("9. 物业服务企业的权利和义务\n");
        prompt.append("10. 业主的权利和义务\n");
        prompt.append("11. 装修管理\n");
        prompt.append("12. 安全管理\n");
        prompt.append("13. 合同变更、解除和终止的条件\n");
        prompt.append("14. 违约责任\n");
        prompt.append("15. 不可抗力\n");
        prompt.append("16. 争议解决方式\n");
        prompt.append("17. 其他条款\n");
        prompt.append("\n请确保生成的物业服务合同符合《中华人民共和国民法典》第九百四十四条至第九百五十条的规定，以及《物业管理条例》等相关法规的要求，语言准确、条款清晰、结构完整。");
        
        return prompt.toString();
    }
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.P_SERVICE_CONTRACT;
    }
}
