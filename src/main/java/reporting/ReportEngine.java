package reporting;

import model.AttendanceRecord;
import model.Employee;
import model.PaySlip;
import model.PayrollManager;

import java.time.Month;
import java.util.List;
import java.util.Map;

public class ReportEngine {

    private PayrollManager payrollManager;
    private List<Employee> employeeRepo;

    public ReportEngine(PayrollManager payrollManager, List<Employee> employeeRepo){

        this.payrollManager=payrollManager;
        this.employeeRepo=employeeRepo;
    }

    public void salarySummaryReport(){

        System.out.println("======== Employee's Information ========");
        for(PaySlip p: payrollManager.getPayrollHistory() ){
            System.out.println("Name: "+p.getEmployee().getName()+" \n Gross Salary: " +
                    ""+p.getGrossSalary()+" \nNet Salary: "+p.getNetSalary());
        }
    }

    public void deptPayrollReport(){

        System.out.println("======== Salary by department ========");
        for (Map.Entry<String , Double> entry : payrollManager.getTotalPaidByDept().entrySet()){
            System.out.println("Department: "+entry.getKey()+
                    " \nTotal Salary: "+entry.getValue());
        }
    }

    public void attendanceSalaryReport(Month month){
        System.out.println("======== Attendance & Salary Report: " + month + " ========");
        for(PaySlip p: payrollManager.getPayrollHistory()){
            Employee emp = p.getEmployee();
            long presentDays = emp.getAttendanceRecords().stream()
                    .filter(r -> r.getDate().getMonth() == month)
                    .count();
            System.out.println("Name: " + emp.getName() +
                    " | Days Recorded: " + presentDays +
                    " | Net Salary: " + p.getNetSalary());
        }
    }

    public void payslipHistoryReport(){
        System.out.println("======== Payslip History ========");
        for(PaySlip p: payrollManager.getPayrollHistory()){
            System.out.println("ID: " + p.getPaySlipId() +
                    " | Employee: " + p.getEmployee().getName() +
                    " | Month: " + p.getMonth() +
                    " | Net: " + p.getNetSalary());
        }
    }

    public void getTotalPaidReport(){
        System.out.println("======== Total Paid by Department ========");
        for(Map.Entry<String, Double> entry : payrollManager.getTotalPaidByDept().entrySet()){
            System.out.println("Department: " + entry.getKey() + " | Total Paid: " + entry.getValue());
        }
    }
}
