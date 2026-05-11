package model;

import enums.EmployeeRole;

public class PartTimeEmployee extends Employee{

    private double hourlyRate;
    private int hoursWorked;


    public PartTimeEmployee(int employeeId, String name, int age, String department,
                            double baseSalary, EmployeeRole role, double hourlyRate ,int hoursWorked){

        super(employeeId, name, age, department, baseSalary, role);

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
