package com.easycontract.controller;

import com.easycontract.entity.dto.ContractRulesDTO;
import com.easycontract.entity.vo.Response;
import com.easycontract.service.ContractRuleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contract/rule")
@CrossOrigin(origins = "*") // 添加CORS支持
public class ContractRuleController {

    @Resource
    private ContractRuleService contractRuleService;

    @PostMapping("/insert")
    public Response<Integer> insertContractRule(@RequestBody ContractRulesDTO contractRulesDTO) {
        Integer count = contractRuleService.insertContractRule(contractRulesDTO);
        return Response.success(count);
    }

    @GetMapping("/get")
    public Response getContractRule() {
        List<ContractRulesDTO> contractRules = contractRuleService.getContractRules();
        Response response = Response.success(contractRules);
        return response;
    }

    @PostMapping("/update")
    public Response<Integer> updateContractRule(@RequestBody ContractRulesDTO contractRulesDTO) {
        Integer count = contractRuleService.updateContractRule(contractRulesDTO);
        return Response.success(count);
    }

    @DeleteMapping("/delete")
    public Response<Integer> deleteContractRule(int code) {
        Integer count = contractRuleService.deleteContractRule(code);
        return Response.success(count);
    }
}
