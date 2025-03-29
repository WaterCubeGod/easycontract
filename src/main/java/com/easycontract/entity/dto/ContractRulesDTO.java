package com.easycontract.entity.dto;

import com.easycontract.entity.po.ContractLaw;
import com.easycontract.entity.po.ContractRulePO;

import java.util.List;

public class ContractRulesDTO {

    private int id;

    private int code;

    private String name;

    private String description;

    private List<ContractLaw> contractLaw;

    /**
     * dto转po
     * @param
     * @return
     */
    public ContractRulePO contractRulesDTOtoPO() {
        ContractRulePO contractRulePO = new ContractRulePO();
        contractRulePO.setId(this.getId());
        contractRulePO.setCode(this.getCode());
        contractRulePO.setName(this.getName());
        contractRulePO.setDescription(this.getDescription());
        return contractRulePO;
    }

    public static ContractRulesDTO comboContractRulesDTO(ContractRulePO contractRulePO, List<ContractLaw> contractLaws) {
        ContractRulesDTO contractRulesDTO = new ContractRulesDTO();
        contractRulesDTO.setId(contractRulePO.getId());
        contractRulesDTO.setCode(contractRulePO.getCode());
        contractRulesDTO.setName(contractRulePO.getName());
        contractRulesDTO.setDescription(contractRulePO.getDescription());
        contractRulesDTO.setContractLaw(contractLaws);
        return contractRulesDTO;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ContractLaw> getContractLaw() {
        return contractLaw;
    }

    public void setContractLaw(List<ContractLaw> contractLaw) {
        this.contractLaw = contractLaw;
    }

    @Override
    public String toString() {
        return "ContractRulesDTO{" +
                "id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", contractLaw=" + contractLaw +
                '}';
    }
}
