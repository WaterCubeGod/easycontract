package com.easycontract.entity.enums;

/**
 * 典型合同
 */
public enum ContractEnum {

    UNKOWN_CONTRACT(-1, "无名合同"),

    NULL_CONTRACT(0, "空"),

    SALES_CONTRACT(1, "买卖合同"),

    EWGH_CONTRACT(2, "供用电、水、气、热力合同"),

    GIFT_CONTRACT(3, "赠与合同"),

    LOAN_CONTRACT(4, "借款合同"),

    GUARANTEE_CONTRACT(5, "保证合同"),

    LEASE_CONTRACT(6, "租赁合同"),

    F_LEASE_CONTRACT(7, "融资租赁合同"),

    FACTORING_CONTRACT(8, "保理合同"),

    WORK_CONTRACT(9, "承揽合同"),

    CONSTRUCTION_CONTRACTS(10, "建设工程合同"),

    CARRIAGE_CONTRACTS(11, "运输合同"),

    TECHNOLOGY_CONTRACT(12, "技术合同"),

    CUSTODIAL_CONTRACTS(13, "保管合同"),

    WAREHOUSING_CONTRACT(14, "仓储合同"),

    COMMISSION_CONTRACT(15, "委托合同"),

    P_SERVICE_CONTRACT(16, "物业服务合同"),

    BROKERAGE_CONTRACT(17, "行纪合同"),

    INTERMEDIARY_CONTRACT(18, "中介合同"),

    PARTNERSHIP_CONTRACT(19, "合伙合同"),
    ;

    private int code;

    private String desc;

    ContractEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ContractEnum getContractEnum(int code) {
        for (ContractEnum contractEnum : ContractEnum.values()) {
            if (contractEnum.getCode() == code) {
                return contractEnum;
            }
        }
        return NULL_CONTRACT;
    }

    public static ContractEnum getContractEnum(String desc) {
        for (ContractEnum contractEnum : ContractEnum.values()) {
            if (contractEnum.getDesc().equals(desc)) {
                return contractEnum;
            }
        }
        return NULL_CONTRACT;
    }
}
