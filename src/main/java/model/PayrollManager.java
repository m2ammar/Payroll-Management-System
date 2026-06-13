package model;

import java.time.YearMonth;
import java.util.*;

public class PayrollManager {

    private int nextPayslipId=1;
    private ArrayList<PaySlip> payrollHistory;
    private YearMonth month;
    private List<Employee> employees;
    //“I used Map because it allows me to store key-value pairs, which is ideal for grouping
    //and representing related data such as department-wise salary totals and overall payroll statistics.”

    public PayrollManager(YearMonth month, List<Employee> employees){

        this.payrollHistory = new ArrayList<>();
        if(month == null){
            throw new IllegalArgumentException("Month can't be null.");
        }else{
            this.month=month;
        }
        this.employees = employees;
    }

    public void processMonthly(){

        for(Employee employee : employees){
            double gross =  employee.calculateSalary();
            double  net = employee.computeNetSalary();
            String id = "PS-" + month + "-" + (nextPayslipId++);
            PaySlip slip = new PaySlip(id, employee, month, gross, net);
            payrollHistory.add(slip);
        }
    }

    public PaySlip getPayslipByEmployee(Employee employee, YearMonth targetMonth) {
        for (PaySlip p : payrollHistory) {
            if (p.getEmployee().equals(employee) && p.getMonth().equals(targetMonth)) {
                return p;
            }
        }
        return null;
    }

    public PaySlip getPayslipById(String payslipId) {

        for (PaySlip p : payrollHistory) {

            if (p.getPaySlipId().equals(payslipId)) {
                return p;
            }
        }return null;
    }

    public Map<String , Double > getTotalPaidByDept(){

        Map<String, Double> result = new LinkedHashMap<>();
        for (PaySlip p : payrollHistory) {
            String dept= p.getEmployee().getDepartment();
            double net= p.getNetSalary();
            result.merge(dept, net, Double::sum);
        }return result;
    }

    public Map<String , Double> getOverallSalaryStats(){
        Map<String, Double> result = new LinkedHashMap<>();

        if(payrollHistory.isEmpty()) {
            return result;

        }else{
            double total=0, high=Double.MIN_VALUE, low=Double.MAX_VALUE, avg=0;
            for(PaySlip p : payrollHistory){

                total= total + p.getNetSalary();
                if (p.getNetSalary()> high) {
                    high = p.getNetSalary();
                }
                if(p.getNetSalary()<low) {
                    low = p.getNetSalary();
                }
            }avg= total/payrollHistory.size();
            result.put("total", total);
            result.put("highest", high);
            result.put("lowest", low);
            result.put("average", avg);
        }return result;
    }

    public ArrayList<PaySlip> getPayrollHistory(){
         return this.payrollHistory;
    }

    public YearMonth getMonth(){
        return this.month;
    }

    public List<Employee> getEmployees(){
        return this.employees;
    }
}
