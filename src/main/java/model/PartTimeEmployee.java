package model;

import enums.EmployeeRole;

public class PartTimeEmployee extends Employee{



    public PartTimeEmployee(int employeeId, String name, int age, String department, double baseSalary, EmployeeRole role){

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
