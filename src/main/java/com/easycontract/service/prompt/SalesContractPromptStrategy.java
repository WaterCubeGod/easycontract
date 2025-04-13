package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 买卖合同提示词策略
 */
@Component
public class SalesContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.SALES_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的买卖合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 买卖双方信息" +
        "3. 标的物描述（包括名称、规格、数量、质量要求等）" +
        "4. 价款与支付方式" +
        "5. 交付方式、时间和地点" +
        "6. 检验标准与方法" +
        "7. 所有权转移" +
        "8. 风险责任" +
        "9. 违约责任" +
        "10. 争议解决方式" +
        "11. 其他条款" +
        "请确保生成的买卖合同符合《中华人民共和国民法典》相关规定，语言准确、条款清晰、结构完整。";
    }
}
