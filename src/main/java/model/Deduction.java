package model;

import enums.DeductionType;

public class Deduction {

    private double deduction;
    private DeductionType deductionType;

    public Deduction(double deduction, DeductionType deductionType){
        this.deduction=deduction;
        this.deductionType=deductionType;
    }

    public double calculateDeduction(){
        return this.deduction;
    }

    public double getDeduction(){
        return this.deduction;
    }

    public DeductionType getDeductionType(){
        return this.deductionType;
    }
}
