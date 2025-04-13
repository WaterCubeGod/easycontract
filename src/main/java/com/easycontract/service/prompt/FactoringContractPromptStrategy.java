package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 保理合同提示词策略
 */
@Component
public class FactoringContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.FACTORING_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的保理合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 保理商和债权人信息" +
        "3. 基础交易合同描述" +
        "4. 应收账款的转让范围和方式" +
        "5. 保理融资金额和条件" +
        "6. 保理费用和利息" +
        "7. 还款方式和期限" +
        "8. 应收账款管理和催收" +
        "9. 债权人的陈述与保证" +
        "10. 通知义务" +
        "11. 违约责任" +
        "12. 合同解除条件" +
        "13. 争议解决方式" +
        "14. 其他条款" +
        "请确保生成的保理合同符合《中华人民共和国民法典》第七百六十一条至第七百六十九条的规定，以及相关金融法规的要求，语言准确、条款清晰、结构完整。";
    }
}
