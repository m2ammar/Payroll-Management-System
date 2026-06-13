package model;

import enums.EmployeeRole;

public class PartTimeEmployee extends Employee{

    private double hourlyRate;
    private int hoursWorked;

    // removed int employeeId from params
    public PartTimeEmployee(String name, int age, String department,
                            double baseSalary, EmployeeRole role, double hourlyRate , int hoursWorked){

        super(name, age, department, baseSalary, role); //  no more employeeId

        if(hourlyRate>0) {
            this.hourlyRate = hourlyRate;
        }else {
            throw new IllegalArgumentException("Rate can't be 0 or negative.");
        }
        if(hoursWorked>=0) {
            this.hoursWorked = hoursWorked;
        }else {
            throw new IllegalArgumentException("Worked hour can't be negative");
        }
    }

    //"Each employee type has a different salary calculation strategy,
    // so I used method overriding to implement polymorphic salary behavior."

    @Override
    public double calculateSalary() {
        double result= this.hourlyRate*this.hoursWorked;
        return result;
    }

    public double getHourlyRate(){
        return this.hourlyRate;
    }

    public int getHoursWorked(){
        return this.hoursWorked;
    }

//    @Override
//    public double computeNetSalary() {
//        return super.computeNetSalary();
//    }

}