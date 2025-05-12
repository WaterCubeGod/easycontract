package com.easycontract.service.prompt;

/**
 * 提示词策略接口
 * 使用策略模式处理不同类型的提示词生成
 */
public interface PromptStrategy {
    
    /**
     * 生成提示词
     * @param context 上下文
     * @return 生成的提示词
     */
    default String generatePrompt(String context) {
        StringBuilder prompt = new StringBuilder(AIIdentity());
        // 根据上下文选择合适的合同模板
        prompt.append(contractTemplate(context));
        // 根据不同的合同类型选择不同的提示词
        prompt.append(contractRequest());
        // 填充合同格式要求
        prompt.append(contractFormContext());
        prompt.append("以下是用户上下文：");
        prompt.append(context);
        return prompt.toString();
    }
    
    /**
     * 获取策略类型
     * @return 策略类型
     */
    PromptStrategyType getType();

    String contractRequest();

    default String AIIdentity() {
        return "你是一个专业的法律顾问和合同起草专家，精通中国法律和合同起草。";
    }

    default String contractTemplate(String content) {
        return "合同模板";
    }

    default String contractFormContext() {
        return "同时，合同格式要求如下：" +
                "1. **结构规范**  \n" +
                "   - 按以下层级编写：  \n" +
                "     • 章（如\"第一章 总则\"）  \n" +
                "     • 条（如\"第一条\"）  \n" +
                "     • 款（如\"1.\"）  \n" +
                "     • 项（如\"(1)\"）  \n" +
                "   - 使用以下Markdown标记：  \n" +
                "     # <center>合同名称</center>\n" +
                "     ## 章标题\n" +
                "     ### 条标题\n" +
                "     **1.** 款内容 \n" +
                "       (1) 项内容\n" +
                "\n" +
                "2. **格式标记**  \n" +
                "   - 合同编号：`[cid]`开头左对齐  \n" +
                "   - 表格：用Markdown表格语法，表题前空一行  \n" +
                "   - 附件：用`<!-- pagebreak -->`强制分页  \n" +
                "   - 落款：插入代码块`[signblock]`预留签字区  \n" +
                "\n" +
                "3. **内容要求**  \n" +
                "   - 关键条款用【】包裹  \n" +
                "   - 每章结束后空两行  \n" +
                "4. **注意** \n" +
                "   - 重要！！！如果合同不复杂，不需要分章，直接用条和款即可。 \n" +
                "   - 用户未提及附件，不要添加附件。 \n" +
                "   - 重要！！！合同文件内容（包含所有合同内容）应以\"===合同开始===\"标记开始，以\"===合同结束===\"标记结束\n";
    }
}
