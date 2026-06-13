package auth;

import enums.AccessRole;
import enums.LeaveStatus;
import model.Employee;
import model.LeaveRequest;
import model.PayrollManager;

import java.util.List;

public class SuperAdminAccount extends UserAccount {

    private List<Employee>     allEmployees;
    private List<LeaveRequest> leaveRequests;

    public SuperAdminAccount(String username, String passwordHash,
                             List<Employee> allEmployees,
                             List<LeaveRequest> leaveRequests) {
        super(username, passwordHash, AccessRole.SUPER_ADMIN);
        this.allEmployees  = allEmployees;
        this.leaveRequests = leaveRequests;
    }

    /**
      Creates a new SubAdminAccount and returns it.
      The caller (SuperAdminController) adds it to AppData.subAdminAccounts.
      The shared leaveRequests list is passed automatically so the new sub admin
      can see and act on all leave requests immediately.
     */
    public SubAdminAccount promoteToSubAdmin(String username, String password,
                                             PayrollManager manager) {
        SubAdminAccount newSubAdmin = new SubAdminAccount(
                username, password, manager, leaveRequests);
        System.out.println("Sub Admin created: " + username);
        return newSubAdmin;
    }

    public void viewAllLeaveRequests() {
        if (leaveRequests == null || leaveRequests.isEmpty()) {
            System.out.println("No leave requests.");
            return;
        }
        for (LeaveRequest r : leaveRequests) {
            System.out.println("ID: " + r.getRequestId() +
                    " | " + r.getEmployee().getName() +
                    " | " + r.getLeaveStatus());
        }
    }

    public void approveLeave(LeaveRequest request) {
        if (request == null) throw new IllegalArgumentException("Request cannot be null.");
        request.setLeaveStatus(LeaveStatus.APPROVED);
    }

    public void rejectLeave(LeaveRequest request) {
        if (request == null) throw new IllegalArgumentException("Request cannot be null.");
        request.setLeaveStatus(LeaveStatus.REJECTED);
    }

    public void manageAllEmployees() {
        if (allEmployees == null || allEmployees.isEmpty()) {
            System.out.println("No employees.");
            return;
        }
        for (Employee e : allEmployees) System.out.println(e.getDetails());
    }

    @Override
    public String getUsername() { return this.username; }
}
