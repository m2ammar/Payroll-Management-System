package model;

import java.time.YearMonth;
import java.util.*;

public class PayrollManager {

    private int nextPayslipId=1;
    private ArrayList<PaySlip> payrollHistory;
    private YearMonth month;

    public PayrollManager(YearMonth month){

        this.payrollHistory = new ArrayList<>();
        if(month == null){
            throw new IllegalArgumentException("Month can't be null.");
        }else{
            this.month=month;
        }
    }

    public void processMonthly(List<Employee> employees){

        for(Employee employee : employees){
            double gross =  employee.calculateSalary();
            double  net = employee.computeNetSalary();
            String id = "PS-" + month + "-" + (nextPayslipId++);
            PaySlip slip = new PaySlip(id, employee, month, gross, net);
            payrollHistory.add(slip);
            //Loop through payrollHistory. For each payslip,
            // check if its ID matches the ID we're looking for. If yes, return that payslip.
            // If loop ends with no match, return null.
        }
    }

    public PaySlip getPayslipById(String payslipId) {

        for (PaySlip p : payrollHistory) {

            if (p.getPaySlipId().equals(payslipId)) {
                return p;
            }
        }return null;
    }

    public Map<String , Double > getTotalPaidByDept(){

        //A map stores pairs — every key has a value. Here result is storing department name paired with its total salary.
        Map<String, Double> result = new LinkedHashMap<>();
        for (PaySlip p : payrollHistory) {
            String dept= p.getEmployee().getDepartment();
            double net= p.getNetSalary();
            result.merge(dept, net, Double::sum);
        }return result;
        //So if Engineering already has 80000 and you hit another Engineering employee with 40000,
        // it becomes 120000. That's the Double::sum part — it tells merge how to combine the old and new value.
        //Department lives inside the Employee object, not the payslip.
        //So you have to go through the employee first — p.getEmployee().getDepartment(). but salary lives in payslip
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
            result.put("high", high);
            result.put("low", low);
            result.put("average", avg);
        }return result;
    }

    public ArrayList<PaySlip> getPayrollHistory(){
         return this.payrollHistory;
    }

    public YearMonth getMonth(){
        return this.month;
    }
}
