package com.easycontract.service;

import com.easycontract.entity.dto.ContractRulesDTO;

import java.util.List;

public interface ContractRuleService {

    /**
     * 添加合同法规
     */
    Integer insertContractRule(ContractRulesDTO contractRulesDTO);

    /**
     * 查看合同法规
     */
    List<ContractRulesDTO> getContractRules();

    /**
     * 修改合同法规
     */
    Integer updateContractRule(ContractRulesDTO contractRulesDTO);

    /**
     * 删除合同法规
     */
    Integer deleteContractRule(int code);

}
