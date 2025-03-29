package com.easycontract.domain;

import com.easycontract.entity.dto.ContractRulesDTO;
import com.easycontract.entity.enums.ContractEnum;
import com.easycontract.entity.po.ContractLaw;
import com.easycontract.entity.po.ContractRulePO;
import com.easycontract.mapper.ContractRuleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.easycontract.entity.enums.ContractEnum.NULL_CONTRACT;

@Component
public class ContractRuleDomain {

    @Resource
    private ContractRuleMapper contractRuleMapper;

    @Transactional(rollbackFor = Exception.class)
    public Integer insertContractRule(ContractRulesDTO contractRulesDTO) {
        ContractRulePO contractRulePO = contractRulesDTO.contractRulesDTOtoPO();
        List<ContractLaw> contractLaws = contractRulesDTO.getContractLaw();
        contractLaws.forEach((contractLaw -> contractLaw.setContractCode(contractRulePO.getCode())));
        Integer count1 = contractRuleMapper.insertContractRule(contractRulePO);
        if (count1 == 0) {
            // todo
            throw new RuntimeException();
        }

        Integer count2 = 0;
        for (ContractLaw contractLaw : contractLaws) {
            Integer count = contractRuleMapper.insertContractLaw(contractLaw);
            if (count == 0) {
                // todo
                throw new RuntimeException();
            }
            count2 += count;
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer insertContractRule(List<ContractRulesDTO> contractRulesDTOS) {
        Integer sum = 0;
        for (ContractRulesDTO contractRulesDTO : contractRulesDTOS) {
            Integer count = insertContractRule(contractRulesDTO);
            sum += count;
        }
        return sum;
    }

    public ContractRulesDTO getContractRule(Integer code) {
        if (NULL_CONTRACT.equals(ContractEnum.getContractEnum(code))) {
            // todo
            throw new RuntimeException();
        }

        ContractRulePO contractRulePO = contractRuleMapper.getContractRule(code);
        List<ContractLaw> contractLaws = contractRuleMapper.getContractLaw(code);
        if (contractRulePO == null) {
            // todo
            return null;
        }
        if (contractLaws == null || contractLaws.isEmpty()) {
            // todo
            return null;
        }
        return ContractRulesDTO.comboContractRulesDTO(contractRulePO, contractLaws);
    }

    public List<ContractRulesDTO> getContractRule() {
        List<ContractRulePO> contractRulesPOs = contractRuleMapper.getAllContractRule();
        List<ContractRulesDTO> contractRulesDTOList = new ArrayList<>();

        for (ContractRulePO contractRulePO : contractRulesPOs) {
            List<ContractLaw> contractLaws = contractRuleMapper.getContractLaw(contractRulePO.getCode());
            contractRulesDTOList.add(ContractRulesDTO.comboContractRulesDTO(contractRulePO, contractLaws));
        }


        return contractRulesDTOList;
    }

    public Integer updateContractRule(ContractRulesDTO contractRulesDTO) {
        int sum = 0;
        ContractRulePO contractRulePO = contractRulesDTO.contractRulesDTOtoPO();
        ContractRulePO oldContractRulePO = contractRuleMapper.getContractRule(contractRulePO.getCode());
        if (!Objects.equals(contractRulePO, oldContractRulePO)) {
            contractRuleMapper.updateContractRule(contractRulePO);
            sum ++;
        }
        List<ContractLaw> contractLaws = contractRulesDTO.getContractLaw();
        if (contractLaws == null || contractLaws.isEmpty()) {
            return 0;
        }

        List<ContractLaw> oldContractLaws = contractRuleMapper.getContractLaw(contractRulePO.getCode());

        for (ContractLaw contractLaw : contractLaws) {
            Optional<ContractLaw> find = oldContractLaws.stream().filter(id -> id.getId() == contractLaw.getId()).findFirst();
            if (find.isPresent()) {
                ContractLaw oldContractLaw = find.get();
                if (!Objects.equals(contractLaw, oldContractLaw)) {
                    contractRuleMapper.updateContractLaw(contractLaw);
                    sum ++;
                }
                oldContractLaws.remove(oldContractLaw);
            } else {
                contractRuleMapper.insertContractLaw(contractLaw);
                sum ++;
            }
        }
        if (!oldContractLaws.isEmpty()) {
            oldContractLaws.forEach(oldContractLaw -> contractRuleMapper.deleteContractLaw(oldContractLaw.getId()));
        }
        return sum;
    }


    public Integer deleteContractRule(Integer code) {
        ContractRulePO contractRulePO = contractRuleMapper.getContractRule(code);
        if (contractRulePO == null) {
            return 0;
        }
        int sum = 0;
        sum += contractRuleMapper.deleteContractLawByCode(contractRulePO.getCode());
        sum += contractRuleMapper.deleteContractRule(contractRulePO.getId());
        return sum;
    }
}
