package com.easycontract.entity.po;

import java.util.Objects;

public class ContractLaw {

    private int id;

    private int contractCode;

    private String lawName;

    private String article;

    private String law;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContractCode() {
        return contractCode;
    }

    public void setContractCode(int contractCode) {
        this.contractCode = contractCode;
    }

    public String getLawName() {
        return lawName;
    }

    public void setLawName(String lawName) {
        this.lawName = lawName;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractLaw that = (ContractLaw) o;
        return id == that.id && contractCode == that.contractCode && Objects.equals(lawName, that.lawName) && Objects.equals(article, that.article) && Objects.equals(law, that.law);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contractCode, lawName, article, law);
    }

    @Override
    public String toString() {
        return "ContractLaw{" +
                "id=" + id +
                ", contractCode=" + contractCode +
                ", lawName='" + lawName + '\'' +
                ", article='" + article + '\'' +
                ", law='" + law + '\'' +
                '}';
    }
}
