package com.easycontract.service.prompt;

/**
 * 提示词策略类型
 */
public enum PromptStrategyType {
    GENERAL,                // 通用对话
    CONTRACT_VALIDATION,    // 合同校验
    CONTRACT_CREATION,      // 通用合同生成
    UNKNOWN_CONTRACT,       // 无名合同
    SALES_CONTRACT,         // 买卖合同
    EWGH_CONTRACT,          // 供用电、水、气、热力合同
    GIFT_CONTRACT,          // 赠与合同
    LOAN_CONTRACT,          // 借款合同
    GUARANTEE_CONTRACT,     // 保证合同
    LEASE_CONTRACT,         // 租赁合同
    F_LEASE_CONTRACT,       // 融资租赁合同
    FACTORING_CONTRACT,     // 保理合同
    WORK_CONTRACT,          // 承揽合同
    CONSTRUCTION_CONTRACT,  // 建设工程合同
    CARRIAGE_CONTRACT,      // 运输合同
    TECHNOLOGY_CONTRACT,    // 技术合同
    CUSTODIAL_CONTRACT,     // 保管合同
    WAREHOUSING_CONTRACT,   // 仓储合同
    COMMISSION_CONTRACT,    // 委托合同
    P_SERVICE_CONTRACT,     // 物业服务合同
    BROKERAGE_CONTRACT,     // 行纪合同
    INTERMEDIARY_CONTRACT,  // 中介合同
    PARTNERSHIP_CONTRACT    // 合伙合同
}
