package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 借款合同提示词策略
 */
@Component
public class LoanContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.LOAN_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的借款合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 借款人和贷款人信息" +
        "3. 借款金额（包括大小写金额）" +
        "4. 借款期限" +
        "5. 借款用途" +
        "6. 利率与计息方式" +
        "7. 还款方式与还款计划" +
        "8. 担保方式（如有）" +
        "9. 违约责任" +
        "10. 争议解决方式" +
        "11. 其他条款" +
        "请确保生成的借款合同符合《中华人民共和国民法典》和相关金融法规的规定，语言准确、条款清晰、结构完整。";
    }
}
