package com.easycontract.service.impl;

import com.easycontract.service.PromptEngineeringService;
import org.springframework.stereotype.Service;

/**
 * 提示词工程服务实现类
 */
@Service
public class PromptEngineeringServiceImpl implements PromptEngineeringService {
    
    @Override
    public String generateGeneralPrompt(String context) {
        // 通用对话的提示词
        return context + "\n\n请回答用户的问题。";
    }
    
    @Override
    public String generateContractValidationPrompt(String context, String contractContent) {
        // 合同校验的提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是需要校验的合同内容：\n");
        prompt.append(contractContent);
        prompt.append("\n\n请对上述合同内容进行全面审核，检查以下方面：\n");
        prompt.append("1. 法律合规性：检查合同条款是否符合相关法律法规\n");
        prompt.append("2. 权责明确性：检查权利义务是否明确，责任划分是否清晰\n");
        prompt.append("3. 逻辑一致性：检查合同内部逻辑是否一致，有无矛盾之处\n");
        prompt.append("4. 语言规范性：检查语言表述是否准确、清晰、无歧义\n");
        prompt.append("5. 风险点识别：识别合同中可能存在的风险点和隐患\n");
        prompt.append("\n请提供详细的审核报告，包括问题描述、风险等级和修改建议。");
        
        return prompt.toString();
    }
    
    @Override
    public String generateContractCreationPrompt(String context, String requirements) {
        // 合同生成的提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append(context);
        prompt.append("\n\n以下是合同生成需求：\n");
        prompt.append(requirements);
        prompt.append("\n\n请根据上述需求生成一份完整的合同，包括但不限于以下部分：\n");
        prompt.append("1. 合同标题\n");
        prompt.append("2. 签约方信息\n");
        prompt.append("3. 合同目的\n");
        prompt.append("4. 权利与义务\n");
        prompt.append("5. 合同期限\n");
        prompt.append("6. 违约责任\n");
        prompt.append("7. 争议解决\n");
        prompt.append("8. 其他条款\n");
        prompt.append("\n请确保生成的合同语言准确、条款清晰、结构完整，并符合中国法律法规。");
        
        return prompt.toString();
    }
}
