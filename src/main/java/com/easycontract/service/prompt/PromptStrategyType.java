package com.easycontract.service.prompt;

/**
 * 提示词策略类型
 */
public enum PromptStrategyType {
    GENERAL,                // 通用对话
    CONTRACT_VALIDATION,    // 合同校验
    CONTRACT_CREATION,      // 通用合同生成
    SALES_CONTRACT,         // 买卖合同
    LOAN_CONTRACT,          // 借款合同
    LEASE_CONTRACT,         // 租赁合同
    GUARANTEE_CONTRACT,     // 保证合同
    WORK_CONTRACT,          // 承揽合同
    CONSTRUCTION_CONTRACT,  // 建设工程合同
    TECHNOLOGY_CONTRACT,    // 技术合同
    PARTNERSHIP_CONTRACT    // 合伙合同
}
