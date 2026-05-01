package model;

import enums.LeaveStatus;

import java.time.LocalDate;

public class LeaveRequest {

    private  int requestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Employee employee;
    private LeaveStatus leaveStatus;


    public  LeaveRequest(int requestId, LocalDate startDate, LocalDate endDate, Employee employee ,LeaveStatus leaveStatus){
        this.requestId=requestId;
        this.startDate=startDate;
        this.endDate=endDate;
        this.employee=employee;
        this.leaveStatus=leaveStatus;

    }

    public void setLeaveStatus(LeaveStatus leaveStatus){
        this.leaveStatus=leaveStatus;
    }

    public boolean isNearingEnd() {
        return LocalDate.now().isAfter(endDate.minusDays(2));
    }

    public long getDaysRemaining() {
        return LocalDate.now().until(endDate, java.time.temporal.ChronoUnit.DAYS);
    }

    public int getRequestId(){
        return this.requestId;
    }

    public LocalDate getStartDate(){
        return this.startDate;
    }

    public LocalDate getEndDate(){
        return this.endDate;
    }

    public Employee getEmployee(){
        return this.employee;
    }

    public LeaveStatus getLeaveStatus(){
        return this.leaveStatus;
    }

}
