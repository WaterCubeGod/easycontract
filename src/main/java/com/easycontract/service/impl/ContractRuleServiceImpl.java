package com.easycontract.service.impl;

import com.easycontract.domain.ContractRuleDomain;
import com.easycontract.entity.dto.ContractRulesDTO;
import com.easycontract.service.ContractRuleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("contractRuleService")
public class ContractRuleServiceImpl implements ContractRuleService {

    @Resource
    private ContractRuleDomain contractRuleDomain;


    @Override
    public Integer insertContractRule(ContractRulesDTO contractRulesDTO) {
        return contractRuleDomain.insertContractRule(contractRulesDTO);
    }

    @Override
    public List<ContractRulesDTO> getContractRules() {
        return contractRuleDomain.getContractRule();
    }

    @Override
    public Integer updateContractRule(ContractRulesDTO contractRulesDTO) {
        return contractRuleDomain.updateContractRule(contractRulesDTO);
    }

    @Override
    public Integer deleteContractRule(int code) {
        return contractRuleDomain.deleteContractRule(code);
    }
}
