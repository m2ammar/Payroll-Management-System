package auth;

import enums.AccessRole;
import enums.LeaveStatus;
import model.AttendanceRecord;
import model.Employee;
import model.LeaveRequest;
import model.PaySlip;
import model.PayrollManager;
import reporting.ReportEngine;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

public class EmployeeAccount extends UserAccount {
    //"HOW the employee logs into the system"

    private Employee employee;
    //is employee here just datatype getting the main details from Employee class then passing object to EmployeeAccount?
    //YES YES YES. Exactly.
    //Understand This Properly
    //private Employee employee;
    //means:EmployeeAccount HAS-A Employee object.
    //This variable is NOT creating employee details itself.
    //It only stores REFERENCE of an Employee object.
    private PayrollManager payrollManager;

    public EmployeeAccount(String username, String passwordHash, AccessRole role, Employee employee) {
        super(username, passwordHash, role);
        this.employee = employee;
    }

    public boolean login(String enteredUsername, String enteredPassword) {
        boolean result = super.login(enteredUsername, enteredPassword);
        if (result) {
            System.out.println("Login successful for: " + this.username);
        } else {
            System.out.println("Login failed for: " + enteredUsername);
        }
        return result;
    }

    public void setPayrollManager(PayrollManager payrollManager) {
        this.payrollManager = payrollManager;
    }

    public void viewOwnPayslip(YearMonth month) {
        if (payrollManager == null) {
            System.out.println("Payroll manager not set.");
            return;
        }
        PaySlip slip = payrollManager.getPayslipByEmployee(employee, month);
        if (slip == null) {
            System.out.println("No payslip found for " + employee.getName() + " in " + month);
        } else {
            System.out.println("Payslip ID: " + slip.getPaySlipId() +
                    " | Gross: " + slip.getGrossSalary() +
                    " | Net: " + slip.getNetSalary());
        }
    }

    public void viewAttendance(Month month) {
        System.out.println("Attendance for " + employee.getName() + " in " + month + ":");
        for (AttendanceRecord r : employee.getAttendanceRecords()) {
            if (r.getDate().getMonth() == month) {
                System.out.println("  " + r.getDate() + " | " + r.getAttendanceStatus() + " | Hours: " + r.getHourWorked());
            }
        }
    }

    public void generateOwnReport() {
        System.out.println("===== Employee Report =====");
        System.out.println(employee.getDetails());
        System.out.println("Gross Salary: " + employee.calculateSalary());
        System.out.println("Net Salary:   " + employee.computeNetSalary());
    }

    public void submitLeaveRequest(int requestId, LocalDate startDate, LocalDate endDate) {
        LeaveRequest request = new LeaveRequest(requestId, startDate, endDate, employee, LeaveStatus.PENDING);
        //Because a leave request is a NEW thing being created.
        employee.addLeaveRequest(request);
        System.out.println("Leave request submitted for " + employee.getName() +
                " from " + startDate + " to " + endDate);
    }

    public String getUsername() {
        return this.username;
    }
    public Employee getEmployee() {
        return this.employee;
    }
}
