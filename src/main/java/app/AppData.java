package app;

import auth.EmployeeAccount;
import auth.SubAdminAccount;
import auth.SuperAdminAccount;
import model.Employee;
import model.LeaveRequest;
import model.PayrollManager;

import java.util.List;

public class AppData {
    //AppData acts as a central in-memory data container that stores
    // and shares all system-wide objects like employees, accounts,
    // and leave requests, while the actual data is created and updated
    // by other classes during program execution.


    public PayrollManager       manager;
    public SuperAdminAccount    superAdmin;
    public List<SubAdminAccount> subAdminAccounts;   // dynamic — Super Admin can add more
    public List<EmployeeAccount> employeeAccounts;
    public List<Employee>        allEmployees;
    public List<LeaveRequest>    allLeaveRequests;

    public AppData(PayrollManager manager,
                   SuperAdminAccount superAdmin,
                   List<SubAdminAccount> subAdminAccounts,
                   List<EmployeeAccount> employeeAccounts,
                   List<Employee> allEmployees,
                   List<LeaveRequest> allLeaveRequests) {

        this.manager          = manager;
        this.superAdmin       = superAdmin;
        this.subAdminAccounts = subAdminAccounts;
        this.employeeAccounts = employeeAccounts;
        this.allEmployees     = allEmployees;
        this.allLeaveRequests = allLeaveRequests;
    }
}
