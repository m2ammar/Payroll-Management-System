package model;

import enums.EmployeeRole;

import java.time.LocalDate;

public class ContractEmployee extends Employee{

    private double contractRate;
    private LocalDate contractEnd;

    //  removed int employeeId from params
    public ContractEmployee(String name, int age, String department,
                            double baseSalary, EmployeeRole role, double contractRate, LocalDate contractEnd){

        super(name, age, department, baseSalary, role); //  no more employeeId
        if(contractRate>0){
            this.contractRate=contractRate;
        }else{
            throw new IllegalArgumentException("Invalid rate");
        }
        if(contractEnd == null){
            throw new IllegalArgumentException("Date can't be null");
        }else{
            this.contractEnd = contractEnd;
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