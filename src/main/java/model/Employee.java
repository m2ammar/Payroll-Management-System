package model;

import interfaces.IPayable;
import interfaces.ISalaryCalc;
import enums.EmployeeRole;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public abstract class Employee  implements IPayable, ISalaryCalc{

    private int employeeId;
    private String name;
    private int age;
    private String department;
    private double baseSalary;
    private EmployeeRole role;

    private  List<AttendanceRecord> attendanceRecords; //aggregation
    private List<LeaveRequest> leaveRequests;  //aggregation
    private List<Allowance> allowances;  //composition
    private List<Deduction> deductions;  //composition

    public Employee(int employeeId, String name, int age, String department, double baseSalary, EmployeeRole role){

        this.employeeId=employeeId;
        this.name=name;
        this.age=age;
        this.department=department;
        this.baseSalary=baseSalary;
        this.role=role;
        this.attendanceRecords= new ArrayList<>();
        this.leaveRequests= new ArrayList<>();
        this.allowances= new ArrayList<>();
        this.deductions= new ArrayList<>();
    }


    @Override
    public double computeNetSalary() {
        double gross = calculateSalary();
        double totalDeductions = 0;
        for (Deduction d : deductions) {
            totalDeductions += d.getDeduction();
        }
        return gross - totalDeductions;
    }


    public abstract double calculateSalary();

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

    public EmployeeRole getRole(){
        return this.role;
    }

    public List<AttendanceRecord> getAttendanceRecords(){
        return  Collections.unmodifiableList(attendanceRecords);
    }

    public  List<LeaveRequest> getLeaveRequests(){
        return  Collections.unmodifiableList(leaveRequests);
    }

    public List<Allowance> getAllowances(){
        return Collections.unmodifiableList(allowances); //direct return of list will make it easy for everyone to modify it
                                                        // thus return it as an unmodifiable
    }

    public List<Deduction> getDeductions(){
        return Collections.unmodifiableList(deductions);
    }

    public void setName(String name){
        this.name=name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDepartment(String department){
        this.department=department;
    }

    public void setBaseSalary(double baseSalary){
        this.baseSalary=baseSalary;
    }

    public void setRole(EmployeeRole role){
        this.role=role;
    }

    //List management methods
    public boolean addAttendanceRecord(AttendanceRecord r){
        return this.attendanceRecords.add((r));

    }
    public boolean addLeaveRequest(LeaveRequest l){

        return this.leaveRequests.add(l);
    }
    public boolean addAllowance(Allowance a){

        return this.allowances.add(a);
    }
    public boolean addDeduction(Deduction d){

        return this.deductions.add(d);
    }


    public String getDetails() {

        return  "EmployeeID: "+this.employeeId+" \nName: "+this.name+" \nAge: "+this.age+" \nDepartment: "+
                this.department+" \nBaseSalary: "+
                this.baseSalary+" \nRole: "+this.role;
    }


    // TODO: implement getPayslip() after PaySlip class is complete

}
