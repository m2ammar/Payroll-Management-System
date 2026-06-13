package model;

import enums.AttendanceStatus;

import java.time.LocalDate;


//can also be used to store the current date automatically
public class AttendanceRecord {

    private double hourWorked;
    private AttendanceStatus attendanceStatus;
    private LocalDate date;

    public AttendanceRecord(double hourWorked, AttendanceStatus attendanceStatus, LocalDate date){
        this.hourWorked=hourWorked;
        this.attendanceStatus=attendanceStatus;
        this.date=date;
    }

    public double getHourWorked(){
        return this.hourWorked;
    }
    public void setHourWorked(double hourWorked){
        this.hourWorked=hourWorked;
    }

    public void setDate(LocalDate date){
        this.date=date;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus){
        this.attendanceStatus=attendanceStatus;
    }

    public AttendanceStatus getAttendanceStatus(){
        return this.attendanceStatus;
    }

    public LocalDate getDate(){
        return this.date;
    }
}
