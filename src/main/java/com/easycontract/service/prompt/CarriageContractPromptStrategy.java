package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 运输合同提示词策略
 */
@Component
public class CarriageContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.CARRIAGE_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的运输合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 托运人和承运人信息" +
        "3. 运输货物描述（包括名称、规格、数量、价值等）" +
        "4. 包装要求" +
        "5. 运输方式和路线" +
        "6. 装卸责任" +
        "7. 运输期限" +
        "8. 运费金额、计算方式和支付方式" +
        "9. 货物交付方式" +
        "10. 货物保险" +
        "11. 当事人的权利和义务" +
        "12. 违约责任" +
        "13. 免责条款" +
        "14. 争议解决方式" +
        "15. 其他条款" +
        "请确保生成的运输合同符合《中华人民共和国民法典》第八百零七条至第八百二十五条的规定，以及相关运输法规的要求，语言准确、条款清晰、结构完整。";
    }
}
