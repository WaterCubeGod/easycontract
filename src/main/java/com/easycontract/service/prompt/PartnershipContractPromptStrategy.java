package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 合伙合同提示词策略
 */
@Component
public class PartnershipContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.PARTNERSHIP_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的合伙合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 合伙人信息" +
        "3. 合伙目的和经营范围" +
        "4. 合伙期限" +
        "5. 合伙企业名称和主要经营场所" +
        "6. 合伙人的出资方式、数额和缴付期限" +
        "7. 利润分配和亏损分担方式" +
        "8. 合伙事务的执行" +
        "9. 合伙人会议的议事方式和表决程序" +
        "10. 入伙、退伙的条件和程序" +
        "11. 合伙人的权利和义务" +
        "12. 合伙企业的财产" +
        "13. 合伙企业的债务承担" +
        "14. 合伙企业的解散和清算" +
        "15. 违约责任" +
        "16. 争议解决方式" +
        "17. 其他条款" +
        "请确保生成的合伙合同符合《中华人民共和国民法典》第九百六十六条至第九百八十八条的规定，以及《合伙企业法》等相关法规的要求，语言准确、条款清晰、结构完整。";
    }
}
