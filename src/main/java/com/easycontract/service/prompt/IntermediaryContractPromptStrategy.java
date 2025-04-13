package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 中介合同提示词策略
 */
@Component
public class IntermediaryContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.INTERMEDIARY_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的中介合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 委托人和中介人信息" +
        "3. 中介服务的具体内容和范围" +
        "4. 中介服务期限" +
        "5. 中介报酬及支付条件" +
        "6. 中介费用的承担" +
        "7. 中介服务的完成标准" +
        "8. 中介人的告知义务" +
        "9. 中介人的保密义务" +
        "10. 委托人的协助义务" +
        "11. 中介合同的变更和解除" +
        "12. 违约责任" +
        "13. 争议解决方式" +
        "14. 其他条款" +
        "请确保生成的中介合同符合《中华人民共和国民法典》第九百六十条至第九百六十五条的规定，语言准确、条款清晰、结构完整。";
    }
}
