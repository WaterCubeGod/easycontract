package com.easycontract.entity.vo;

import com.easycontract.entity.enums.ContractEnum;

/**
 * 消息请求实体类
 */
public class MessageRequest {
    private String content;
    private String mode = "chat"; // 默认为普通对话模式
    private Integer contractType = 0; // 合同类型，对应 ContractEnum 的 code

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getContractType() {
        return contractType;
    }

    public void setContractType(Integer contractType) {
        this.contractType = contractType;
    }

    /**
     * 获取合同类型枚举
     * @return 合同类型枚举，如果未指定则返回 NULL_CONTRACT
     */
    public ContractEnum getContractEnum() {
        if (contractType == null) {
            return ContractEnum.NULL_CONTRACT;
        }
        return ContractEnum.getContractEnum(contractType);
    }
}
