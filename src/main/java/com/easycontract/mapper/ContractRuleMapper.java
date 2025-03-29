package com.easycontract.mapper;

import com.easycontract.entity.po.ContractLaw;
import com.easycontract.entity.po.ContractRulePO;

import java.util.List;

public interface ContractRuleMapper {

    Integer insertContractRule(ContractRulePO contractRulePO);

    Integer insertContractLaw(ContractLaw contractLaw);

    ContractRulePO getContractRule(int code);

    List<ContractRulePO> getAllContractRule();

    List<ContractLaw> getContractLaw(int contractCode);

    List<ContractLaw> getAllContractLaw();

    Integer updateContractRule(ContractRulePO contractRulePO);

    Integer updateContractLaw(ContractLaw contractLaw);

    Integer deleteContractRule(int id);

    Integer deleteContractLaw(int id);

    Integer deleteContractLawByCode(int code);
}
