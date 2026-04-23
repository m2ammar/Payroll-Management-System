package model;

public abstract class Employee {

    private int employeeId;
    private String name;
    private int age;
    private String department;
    private double baseSalary;

    public Employee(int employeeId, String name, int age, String department, double baseSalary){

        this.employeeId=employeeId;
        this.name=name;
        this.age=age;
        this.department=department;
        this.baseSalary=baseSalary;
    }

    public int getEmployeeId(){
        return this.employeeId;
    }

    public String getName(){
        return this.name;
    }

    public int getAge(){
        return  this.age;
    }

    public String getDepartment(){
        return this.department;
    }

    public double getBaseSalary(){
        return this.baseSalary;
    }
}
