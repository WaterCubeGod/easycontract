package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 无名合同提示词策略
 */
@Component
public class UnknownContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.UNKNOWN_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的无名合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 签约方信息" +
        "3. 合同目的" +
        "4. 权利与义务" +
        "5. 合同期限" +
        "6. 违约责任" +
        "7. 争议解决方式" +
        "8. 其他条款" +
        "请确保生成的合同符合《中华人民共和国民法典》相关规定，语言准确、条款清晰、结构完整。";
    }
}
