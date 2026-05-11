package model;

import enums.EmployeeRole;

import java.time.LocalDate;

public class ContractEmployee extends Employee{

    private double contractRate;
    private LocalDate contractEnd;

    public ContractEmployee(int employeeId, String name, int age, String department,
                            double baseSalary, EmployeeRole role, double contractRate, LocalDate contractEnd){

        super(employeeId, name, age, department, baseSalary, role);
        if(contractRate>0){
            this.contractRate=contractRate;
        }else{
            throw new IllegalArgumentException("Invalid rate");
        }
         if(contractEnd == null){
            throw new IllegalArgumentException("Date can't be null");
        } else if(contractEnd.isAfter(LocalDate.now())) { //LocalDate — date only, no time. (2026-05-08)
            this.contractEnd = contractEnd;
        }else {
            throw new IllegalArgumentException("Invalid date");
        }
    }


    @Override
    public double calculateSalary() {
        return contractRate;
    }

    public double getContractRate(){
        return this.contractRate;
    }

    public LocalDate getContractEnd(){
        return this.contractEnd;
    }

//    @Override
//    public double computeNetSalary() {
//        return super.computeNetSalary();
//    }

}
