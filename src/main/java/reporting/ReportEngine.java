package reporting;

import model.Employee;
import model.PaySlip;
import model.PayrollManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportEngine {

    private model.PayrollManager payrollManager;
    private List<Employee> employeeRepo;

    public ReportEngine(PayrollManager payrollManager, List<Employee> employeeRepo){

        this.payrollManager=payrollManager;
        this.employeeRepo=employeeRepo;
    }

    public void  salarySummaryReport(){

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
}
