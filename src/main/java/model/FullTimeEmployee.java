package model;

import enums.EmployeeRole;

public class FullTimeEmployee extends Employee{


    public FullTimeEmployee(int employeeId, String name, int age, String department, double baseSalary, EmployeeRole role){

        super(employeeId, name, age, department, baseSalary, role);
    }

    @Override
    public double calculateSalary() {
        return getBaseSalary();
    }

    @Override
    public double computeNetSalary() {
        return super.computeNetSalary();
    }

}
