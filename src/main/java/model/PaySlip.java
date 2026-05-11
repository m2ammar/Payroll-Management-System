package model;

import java.time.YearMonth;

public class PaySlip {

    private String paySlipId;
    private Employee employee;
    private YearMonth month;
    //YearMonth is from java.time — same package as LocalDate, represents just a month and year like 2026-05.
    private double grossSalary;
    private double netSalary;

    public PaySlip(String paySlipId,Employee employee ,YearMonth month, double grossSalary,double netSalary){

        if(paySlipId ==null) {
            throw new IllegalArgumentException("Id can't be null or negative");
        }else {
            this.paySlipId = paySlipId;
        }
        this.employee=employee;
        if(month == null){
            throw new IllegalArgumentException("Month can't be null");
        }else {
            this.month=month;
        }
        if(grossSalary>0){
            this.grossSalary=grossSalary;
        }else{
            throw new IllegalArgumentException("Salary can't be negative or zero.");
        }
        if (netSalary>0){
            this.netSalary=netSalary;
        }else{
            throw new IllegalArgumentException("Net-Salary can't be negative or zero.");
        }
    }

    public void generate(){

        this.grossSalary=this.employee.calculateSalary();
        this.netSalary=this.employee.computeNetSalary();
    }

    public void reGenerate(){
        generate();
    }

    public String getPaySlipId(){
        return this.paySlipId;
    }

    public YearMonth getMonth(){
        return this.month;
    }

    public double getGrossSalary(){
        return this.grossSalary;
    }

    public double getNetSalary(){
        return this.netSalary;
    }

    public Employee getEmployee(){
        return this.employee;
    }

}
