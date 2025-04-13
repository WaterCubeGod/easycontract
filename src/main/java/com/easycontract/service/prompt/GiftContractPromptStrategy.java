package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 赠与合同提示词策略
 */
@Component
public class GiftContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.GIFT_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的赠与合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 赠与人和受赠人信息" +
        "3. 赠与标的物描述（包括名称、数量、价值等）" +
        "4. 赠与的目的和条件（如有）" +
        "5. 赠与的交付方式和时间" +
        "6. 赠与的撤销条件（如有）" +
        "7. 税费承担" +
        "8. 争议解决方式" +
        "9. 其他条款" +
        "请确保生成的赠与合同符合《中华人民共和国民法典》第六百五十七条至第六百六十八条的规定，语言准确、条款清晰、结构完整。";
    }
}
