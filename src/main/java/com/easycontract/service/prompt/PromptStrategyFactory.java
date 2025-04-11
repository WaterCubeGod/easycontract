package com.easycontract.service.prompt;

import com.easycontract.entity.enums.ContractEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提示词策略工厂
 * 使用工厂模式创建不同的提示词策略
 */
@Component
public class PromptStrategyFactory {

    private final Map<PromptStrategyType, PromptStrategy> strategies = new HashMap<>();
    private final Map<ContractEnum, PromptStrategy> contractStrategies = new HashMap<>();

    @Autowired
    public PromptStrategyFactory(List<PromptStrategy> strategyList) {
        // 初始化策略映射
        for (PromptStrategy strategy : strategyList) {
            strategies.put(strategy.getType(), strategy);
        }

        // 初始化合同类型到策略的映射
        initContractStrategies();
    }

    /**
     * 初始化合同类型到策略的映射
     */
    private void initContractStrategies() {
        // 默认使用通用合同生成策略
        PromptStrategy defaultStrategy = strategies.get(PromptStrategyType.CONTRACT_CREATION);

        // 为每种合同类型指定对应的策略
        // 无名合同
        contractStrategies.put(ContractEnum.UNKOWN_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.UNKNOWN_CONTRACT, defaultStrategy));

        // 买卖合同
        contractStrategies.put(ContractEnum.SALES_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.SALES_CONTRACT, defaultStrategy));

        // 供用电、水、气、热力合同
        contractStrategies.put(ContractEnum.EWGH_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.EWGH_CONTRACT, defaultStrategy));

        // 赠与合同
        contractStrategies.put(ContractEnum.GIFT_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.GIFT_CONTRACT, defaultStrategy));

        // 借款合同
        contractStrategies.put(ContractEnum.LOAN_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.LOAN_CONTRACT, defaultStrategy));

        // 保证合同
        contractStrategies.put(ContractEnum.GUARANTEE_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.GUARANTEE_CONTRACT, defaultStrategy));

        // 租赁合同
        contractStrategies.put(ContractEnum.LEASE_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.LEASE_CONTRACT, defaultStrategy));

        // 融资租赁合同
        contractStrategies.put(ContractEnum.F_LEASE_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.F_LEASE_CONTRACT, defaultStrategy));

        // 保理合同
        contractStrategies.put(ContractEnum.FACTORING_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.FACTORING_CONTRACT, defaultStrategy));

        // 承揽合同
        contractStrategies.put(ContractEnum.WORK_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.WORK_CONTRACT, defaultStrategy));

        // 建设工程合同
        contractStrategies.put(ContractEnum.CONSTRUCTION_CONTRACTS,
                strategies.getOrDefault(PromptStrategyType.CONSTRUCTION_CONTRACT, defaultStrategy));

        // 运输合同
        contractStrategies.put(ContractEnum.CARRIAGE_CONTRACTS,
                strategies.getOrDefault(PromptStrategyType.CARRIAGE_CONTRACT, defaultStrategy));

        // 技术合同
        contractStrategies.put(ContractEnum.TECHNOLOGY_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.TECHNOLOGY_CONTRACT, defaultStrategy));

        // 保管合同
        contractStrategies.put(ContractEnum.CUSTODIAL_CONTRACTS,
                strategies.getOrDefault(PromptStrategyType.CUSTODIAL_CONTRACT, defaultStrategy));

        // 仓储合同
        contractStrategies.put(ContractEnum.WAREHOUSING_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.WAREHOUSING_CONTRACT, defaultStrategy));

        // 委托合同
        contractStrategies.put(ContractEnum.COMMISSION_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.COMMISSION_CONTRACT, defaultStrategy));

        // 物业服务合同
        contractStrategies.put(ContractEnum.P_SERVICE_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.P_SERVICE_CONTRACT, defaultStrategy));

        // 行纪合同
        contractStrategies.put(ContractEnum.BROKERAGE_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.BROKERAGE_CONTRACT, defaultStrategy));

        // 中介合同
        contractStrategies.put(ContractEnum.INTERMEDIARY_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.INTERMEDIARY_CONTRACT, defaultStrategy));

        // 合伙合同
        contractStrategies.put(ContractEnum.PARTNERSHIP_CONTRACT,
                strategies.getOrDefault(PromptStrategyType.PARTNERSHIP_CONTRACT, defaultStrategy));

        // 其他合同类型使用默认策略
        for (ContractEnum contractType : ContractEnum.values()) {
            if (!contractStrategies.containsKey(contractType)) {
                contractStrategies.put(contractType, defaultStrategy);
            }
        }
    }

    /**
     * 获取通用对话策略
     */
    public PromptStrategy getGeneralStrategy() {
        return strategies.get(PromptStrategyType.GENERAL);
    }

    /**
     * 获取合同校验策略
     */
    public PromptStrategy getContractValidationStrategy() {
        return strategies.get(PromptStrategyType.CONTRACT_VALIDATION);
    }

    /**
     * 根据合同类型获取合同生成策略
     */
    public PromptStrategy getContractCreationStrategy(ContractEnum contractType) {
        return contractStrategies.getOrDefault(contractType,
                strategies.get(PromptStrategyType.CONTRACT_CREATION));
    }

    /**
     * 根据策略类型获取策略
     */
    public PromptStrategy getStrategy(PromptStrategyType type) {
        return strategies.get(type);
    }
}
