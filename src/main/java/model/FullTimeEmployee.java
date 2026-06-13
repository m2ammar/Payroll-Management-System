package model;

import enums.EmployeeRole;

public class FullTimeEmployee extends Employee{

    private double allowances;
    private int overtimeHrs;
    //Deductions are handled in the parent class to ensure
    // a single unified net salary calculation logic for all
    // employee types, while allowing each type to define its own gross salary."


    public FullTimeEmployee(String name, int age, String department, double baseSalary,
                            EmployeeRole role, double allowances, int overtimeHrs){

        super(name, age, department, baseSalary, role);
        if(allowances>=0) {
            this.allowances = allowances;
        }else {
            throw new IllegalArgumentException("Incorrect Allowance");
        }
        if(overtimeHrs>=0) {
            this.overtimeHrs = overtimeHrs;
        }else{
            throw new IllegalArgumentException("Hour can't be negative");
        }
    }

    @Override
    public double calculateSalary() {
        double result= this.allowances+this.getBaseSalary()+(2000*this.overtimeHrs);
        return result;
    }

    /*The getter name just needs to make sense
    — it's just a regular method that happens to
    return a field. No rule forcing it to match exactly.*/
    public double getTotalAllowances(){
        return this.allowances;
    }

    public int getOvertimeHrs(){
        return this.overtimeHrs;
    }

//    @Override
//    public double computeNetSalary() {
//        return super.computeNetSalary();
//    }

}