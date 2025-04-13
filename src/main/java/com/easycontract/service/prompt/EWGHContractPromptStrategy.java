package com.easycontract.service.prompt;

import org.springframework.stereotype.Component;

/**
 * 供用电、水、气、热力合同提示词策略
 */
@Component
public class EWGHContractPromptStrategy implements PromptStrategy {

    @Override
    public PromptStrategyType getType() {
        return PromptStrategyType.EWGH_CONTRACT;
    }

    @Override
    public String contractRequest() {
        return "请根据下面合同需求和用户需求生成一份完整的供用电、水、气、热力合同，包括但不限于以下部分：" +
        "1. 合同标题" +
        "2. 供应方和用户方信息" +
        "3. 供应内容（电、水、气或热力）" +
        "4. 供应地点和用途" +
        "5. 计量方式和标准" +
        "6. 价格和费用计算方式" +
        "7. 结算方式和期限" +
        "8. 供应方和用户方的权利义务" +
        "9. 服务质量和安全标准" +
        "10. 违约责任" +
        "11. 合同期限和终止条件" +
        "12. 争议解决方式" +
        "13. 其他条款" +
        "请确保生成的合同符合《中华人民共和国民法典》和相关行业法规的规定，语言准确、条款清晰、结构完整。";
    }
}
