package model;

import enums.AllowanceType;

public class Allowance {

    private AllowanceType allowanceType;
    private double amount;

    public Allowance(double amount,  AllowanceType allowanceType){
        this.amount=amount;
        this.allowanceType=allowanceType;
    }

    public double compute(){
        return this.amount;
    }

    public double getAmount(){
        return this.amount;
    }

    public AllowanceType getAllowanceType(){
        return this.allowanceType;
    }
}
