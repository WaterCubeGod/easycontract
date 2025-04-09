package com.easycontract.entity.dto;

import com.easycontract.entity.po.ContractRulePO;
import com.easycontract.entity.po.ContractTemplate;

import java.util.List;

public class ContractTemplates {

    private ContractRulePO contractRulePO;

    List<ContractTemplate> contractTemplates;

    public List<ContractTemplate> getContractTemplates() {
        return contractTemplates;
    }

    public void setContractTemplates(List<ContractTemplate> contractTemplates) {
        this.contractTemplates = contractTemplates;
    }

    public ContractRulePO getContractRulePO() {
        return contractRulePO;
    }

    public void setContractRulePO(ContractRulePO contractRulePO) {
        this.contractRulePO = contractRulePO;
    }
}
