package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 物业服务合同提示词策略
 */
@Component
public class PServiceContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.P_SERVICE_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的物业服务合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 物业服务企业和业主（或业主委员会）信息" +
        "3. 服务区域和项目" +
        "4. 服务内容和标准" +
        "5. 服务期限" +
        "6. 物业服务费用及其调整机制" +
        "7. 费用的支付方式和期限" +
        "8. 物业共用部位和共用设施设备的使用、维护和管理" +
        "9. 物业服务企业的权利和义务" +
        "10. 业主的权利和义务" +
        "11. 装修管理" +
        "12. 安全管理" +
        "13. 合同变更、解除和终止的条件" +
        "14. 违约责任" +
        "15. 不可抗力" +
        "16. 争议解决方式" +
        "17. 其他条款" +
        "请确保生成的物业服务合同符合《中华人民共和国民法典》第九百四十四条至第九百五十条的规定，以及《物业管理条例》等相关法规的要求，语言准确、条款清晰、结构完整。";
    }
}
