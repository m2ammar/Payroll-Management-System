package model;

import enums.AttendanceStatus;

import java.time.LocalDate;

public class AttendanceRecord {

    private int hourWorked;
    private AttendanceStatus attendanceStatus;
    private LocalDate date;

    public AttendanceRecord(int hourWorked, AttendanceStatus attendanceStatus, LocalDate date){
        this.hourWorked=hourWorked;
        this.attendanceStatus=attendanceStatus;
        this.date=date;
    }

    public int getHourWorked(){
        return this.hourWorked;
    }
    public void setHourWorked(int hourWorked){
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
