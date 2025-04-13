package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 委托合同提示词策略
 */
@Component
public class CommissionContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.COMMISSION_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的委托合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 委托人和受托人信息" +
        "3. 委托事项的具体内容和范围" +
        "4. 委托期限" +
        "5. 委托权限的范围" +
        "6. 委托报酬及支付方式" +
        "7. 委托事项的完成标准" +
        "8. 委托费用的承担" +
        "9. 受托人的报告义务" +
        "10. 委托人的协助义务" +
        "11. 委托事项的转委托" +
        "12. 委托合同的变更和解除" +
        "13. 违约责任" +
        "14. 争议解决方式" +
        "15. 其他条款" +
        "请确保生成的委托合同符合《中华人民共和国民法典》第九百一十九条至第九百四十三条的规定，语言准确、条款清晰、结构完整。";
    }
}
