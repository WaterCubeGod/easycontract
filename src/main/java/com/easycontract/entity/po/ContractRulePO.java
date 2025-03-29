package com.easycontract.entity.po;

import java.util.Arrays;
import java.util.Objects;

/**
 * 合同规则
 */
public class ContractRulePO {

    private int id;

    private int code;

    private String name;

    private String description;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractRulePO that = (ContractRulePO) o;
        return id == that.id && code == that.code && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, description);
    }

    @Override
    public String toString() {
        return "ContractRulePO{" +
                "id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
