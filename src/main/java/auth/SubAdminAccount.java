package auth;
import auth.UserAccount;
import enums.AccessRole;
import enums.LeaveStatus;
import model.AttendanceRecord;
import model.Employee;
import model.LeaveRequest;
import model.PayrollManager;
import reporting.ReportEngine;

import java.time.Month;
import java.util.List;

public class SubAdminAccount extends UserAccount {
    private final boolean canPromote = false;
    private PayrollManager payrollManager;
    private List<LeaveRequest> leaveRequests;

    public SubAdminAccount(String username, String passwordHash, PayrollManager payrollManager, List<LeaveRequest> leaveRequests) {
        super(username, passwordHash, AccessRole.SUB_ADMIN);
        this.payrollManager = payrollManager;
        this.leaveRequests = leaveRequests;
    }

    public void managePayroll() {
        if (payrollManager == null) {
            throw new IllegalArgumentException("Payroll Manager cannot be null.");
        }
        System.out.println("Payroll Managed successfully for month: " + payrollManager.getMonth());
    }

    public void viewReports() {
        ReportEngine reportEngine = new ReportEngine(payrollManager, null);
        //Creates object of ReportEngine.Why?
        //Because report-related methods exist inside ReportEngine class.
        //It passes:payrollManager,null
        reportEngine.salarySummaryReport();
        reportEngine.deptPayrollReport();
    }

    public void manageAttendance(Employee employee, Month month) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        List<AttendanceRecord> records = employee.getAttendanceRecords();
        long count = records.stream()
                .filter(r -> r.getDate().getMonth() == month)
                .count();
        System.out.println("Attendance records for " + employee.getName() + " in " + month + ": " + count);
    }

    public void approveLeave(LeaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Leave request cannot be null.");
        }
        request.setLeaveStatus(LeaveStatus.APPROVED);
        System.out.println("Leave request approved.");
    }

    public void rejectLeave(LeaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Leave request cannot be null.");
        }
        request.setLeaveStatus(LeaveStatus.REJECTED);
        System.out.println("Leave request rejected.");
    }

    public void viewAllLeaveRequests() {
        if (leaveRequests == null || leaveRequests.isEmpty()) {
            System.out.println("No leave requests available.");
            return;
        }
        for (LeaveRequest request : leaveRequests) {
            System.out.println("Request ID: " + request.getRequestId() +
                    " | Employee: " + request.getEmployee().getName() +
                    " | Status: " + request.getLeaveStatus());
        }
    }
}
