package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 融资租赁合同提示词策略
 */
@Component
public class FLeaseContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.F_LEASE_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的融资租赁合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 出租人和承租人信息" +
        "3. 租赁物描述（包括名称、规格、数量、价值等）" +
        "4. 租赁物的购买和交付" +
        "5. 租赁期限" +
        "6. 租金金额、支付方式和支付期限" +
        "7. 租赁物的所有权和使用权" +
        "8. 租赁物的维护和保养责任" +
        "9. 租赁物的保险" +
        "10. 租赁期满后租赁物的处理方式" +
        "11. 违约责任" +
        "12. 合同解除条件" +
        "13. 争议解决方式" +
        "14. 其他条款" +
        "请确保生成的融资租赁合同符合《中华人民共和国民法典》第七百三十五条至第七百四十五条的规定，以及相关金融法规的要求，语言准确、条款清晰、结构完整。";
    }
}
