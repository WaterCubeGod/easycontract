package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 行纪合同提示词策略
 */
@Component
public class BrokerageContractPromptStrategy implements PromptStrategy {
    
    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.BROKERAGE_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的行纪合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 委托人和行纪人信息" +
        "3. 行纪事项的具体内容和范围" +
        "4. 行纪权限的范围" +
        "5. 行纪期限" +
        "6. 行纪报酬及支付方式" +
        "7. 行纪费用的承担" +
        "8. 交易的价格区间或定价原则" +
        "9. 行纪人的报告义务" +
        "10. 委托人的协助义务" +
        "11. 行纪人的自己成为对方当事人的条件" +
        "12. 行纪合同的变更和解除" +
        "13. 违约责任" +
        "14. 争议解决方式" +
        "15. 其他条款" +
        "请确保生成的行纪合同符合《中华人民共和国民法典》第九百五十一条至第九百五十九条的规定，语言准确、条款清晰、结构完整。";
    }
}
