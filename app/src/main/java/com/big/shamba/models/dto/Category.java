package com.big.shamba.models.dto;

import com.big.shamba.models.InvestmentPackage;

import java.util.List;

public class Category {
    private String name;
    private List<InvestmentPackage> investmentPackageList;

    public Category() {
    }

    public Category(String name, List<InvestmentPackage> investmentPackageList) {
        this.name = name;
        this.investmentPackageList = investmentPackageList;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InvestmentPackage> getPackageList() {
        return investmentPackageList;
    }

    public void setPackageList(List<InvestmentPackage> investmentPackageList) {
        this.investmentPackageList = investmentPackageList;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", investmentPackageList=" + investmentPackageList +
                '}';
    }
}