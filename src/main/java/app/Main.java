package app;

import auth.*;
import enums.*;
import model.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static AppData initData() {

        // Try loading from CSV first
        DataStore.LoadResult loaded = DataStore.load();

        List<Employee>       allEmployees;
        List<EmployeeAccount> empAccounts;

        if (!loaded.employees().isEmpty()) {
            // CSV exists and has data — use it
            allEmployees = new ArrayList<>(loaded.employees());
            empAccounts  = new ArrayList<>(loaded.accounts());
            System.out.println("[DataStore] Loaded " + allEmployees.size() + " employees from CSV.");
        } else {
            // First run, seed default data and persist it
            allEmployees = buildSeedEmployees();
            empAccounts  = buildSeedAccounts(allEmployees);
            DataStore.saveAll(allEmployees, empAccounts);
            System.out.println("[DataStore] First run — seeded " + allEmployees.size() + " employees and saved to CSV.");
        }

        // Leave requests (runtime only — not persisted)
        List<LeaveRequest> allLeaveRequests = new ArrayList<>();

        LocalDate today    = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        if (allEmployees.size() >= 3) {
            LeaveRequest leave1 = new LeaveRequest(1, nextWeek,             nextWeek.plusDays(5),  allEmployees.get(0), LeaveStatus.PENDING);
            LeaveRequest leave2 = new LeaveRequest(2, nextWeek.plusDays(1), nextWeek.plusDays(3),  allEmployees.get(1), LeaveStatus.PENDING);
            LeaveRequest leave3 = new LeaveRequest(3, nextWeek.plusDays(2), nextWeek.plusDays(10), allEmployees.get(2), LeaveStatus.PENDING);
            allEmployees.get(0).addLeaveRequest(leave1);
            allEmployees.get(1).addLeaveRequest(leave2);
            allEmployees.get(2).addLeaveRequest(leave3);
            allLeaveRequests.add(leave1);
            allLeaveRequests.add(leave2);
            allLeaveRequests.add(leave3);
        }

        // ── Payroll
        PayrollManager manager = new PayrollManager(YearMonth.now(), allEmployees);
        manager.processMonthly();

        for (EmployeeAccount acc : empAccounts)
            acc.setPayrollManager(manager);//every employee can see it

        // ── Admin accounts
        List<SubAdminAccount> subAdminAccounts = new ArrayList<>();
        SubAdminAccount defaultSub = new SubAdminAccount("subadmin1", "subpass1", manager, allLeaveRequests);
        subAdminAccounts.add(defaultSub);

        SuperAdminAccount superAdmin = new SuperAdminAccount("superadmin", "superpass", allEmployees, allLeaveRequests);

        return new AppData(manager, superAdmin, subAdminAccounts, empAccounts, allEmployees, allLeaveRequests);
    }

    // ── Seed data (first-run only) ───────────────────────────────────────────

    private static List<Employee> buildSeedEmployees() {
        List<Employee> list = new ArrayList<>();

        // Full-Time
        list.add(new FullTimeEmployee("Ali Hassan",    25, "IT",      50000, EmployeeRole.FULL_TIME, 5000, 10));
        list.add(new FullTimeEmployee("Sara Khan",     28, "HR",      55000, EmployeeRole.FULL_TIME, 4000,  5));
        list.add(new FullTimeEmployee("Bilal Ahmed",   30, "Finance", 60000, EmployeeRole.FULL_TIME, 6000,  8));
        list.add(new FullTimeEmployee("Ayesha Malik",  26, "IT",      52000, EmployeeRole.FULL_TIME, 4500, 12));
        list.add(new FullTimeEmployee("Usman Tariq",   32, "Finance", 65000, EmployeeRole.FULL_TIME, 7000,  6));
        list.add(new FullTimeEmployee("Fatima Zahra",  27, "HR",      53000, EmployeeRole.FULL_TIME, 3500,  4));
        list.add(new FullTimeEmployee("Hamza Raza",    29, "IT",      58000, EmployeeRole.FULL_TIME, 5500,  9));
        list.add(new FullTimeEmployee("Zainab Ali",    31, "Finance", 62000, EmployeeRole.FULL_TIME, 6500,  7));
        list.add(new FullTimeEmployee("Omar Sheikh",   35, "HR",      70000, EmployeeRole.FULL_TIME, 8000,  3));
        list.add(new FullTimeEmployee("Nadia Hussain", 24, "IT",      48000, EmployeeRole.FULL_TIME, 4000, 11));

        // Part-Time
        list.add(new PartTimeEmployee("Kamran Baig",      22, "IT",      0, EmployeeRole.PART_TIME, 500, 80));
        list.add(new PartTimeEmployee("Sana Mirza",       21, "HR",      0, EmployeeRole.PART_TIME, 450, 70));
        list.add(new PartTimeEmployee("Tariq Mehmood",    23, "Finance", 0, EmployeeRole.PART_TIME, 550, 90));
        list.add(new PartTimeEmployee("Rabia Nawaz",      20, "IT",      0, EmployeeRole.PART_TIME, 480, 75));
        list.add(new PartTimeEmployee("Fahad Iqbal",      24, "HR",      0, EmployeeRole.PART_TIME, 520, 85));
        list.add(new PartTimeEmployee("Hina Qureshi",     22, "Finance", 0, EmployeeRole.PART_TIME, 460, 65));
        list.add(new PartTimeEmployee("Asad Javed",       25, "IT",      0, EmployeeRole.PART_TIME, 510, 88));
        list.add(new PartTimeEmployee("Mariam Farooq",    21, "HR",      0, EmployeeRole.PART_TIME, 490, 72));
        list.add(new PartTimeEmployee("Rizwan Siddiqui",  23, "Finance", 0, EmployeeRole.PART_TIME, 530, 92));
        list.add(new PartTimeEmployee("Amna Butt",        20, "IT",      0, EmployeeRole.PART_TIME, 470, 68));

        // Contract
        list.add(new ContractEmployee("Shahid Afridi",  33, "IT",      0, EmployeeRole.CONTRACT, 80000, LocalDate.of(2027, 3, 31)));
        list.add(new ContractEmployee("Mehwish Hayat",  30, "HR",      0, EmployeeRole.CONTRACT, 75000, LocalDate.of(2027, 2, 28)));
        list.add(new ContractEmployee("Faisal Qureshi", 35, "Finance", 0, EmployeeRole.CONTRACT, 90000, LocalDate.of(2027, 1, 31)));
        list.add(new ContractEmployee("Mahnoor Baloch", 28, "IT",      0, EmployeeRole.CONTRACT, 70000, LocalDate.of(2026, 12, 31)));
        list.add(new ContractEmployee("Danish Taimoor", 32, "HR",      0, EmployeeRole.CONTRACT, 85000, LocalDate.of(2026, 11, 30)));
        list.add(new ContractEmployee("Aiman Zaman",    27, "Finance", 0, EmployeeRole.CONTRACT, 72000, LocalDate.of(2026, 10, 31)));
        list.add(new ContractEmployee("Wahaj Ali",      29, "IT",      0, EmployeeRole.CONTRACT, 78000, LocalDate.of(2026, 9,  30)));
        list.add(new ContractEmployee("Yumna Zaidi",    31, "HR",      0, EmployeeRole.CONTRACT, 82000, LocalDate.of(2026, 8,  31)));

        // Allowances & Deductions for first few employees
        addSeedAllowancesDeductions(list);

        return list;
    }

    private static void addSeedAllowancesDeductions(List<Employee> list) {
        // Allowances
        list.get(0).addAllowance(new Allowance(5000, AllowanceType.HOUSING));
        list.get(0).addAllowance(new Allowance(2000, AllowanceType.TRANSPORT));
        list.get(1).addAllowance(new Allowance(4000, AllowanceType.HOUSING));
        list.get(1).addAllowance(new Allowance(1500, AllowanceType.MEDICAL));
        list.get(2).addAllowance(new Allowance(6000, AllowanceType.HOUSING));
        list.get(2).addAllowance(new Allowance(2500, AllowanceType.TRANSPORT));
        list.get(3).addAllowance(new Allowance(4500, AllowanceType.HOUSING));
        list.get(4).addAllowance(new Allowance(7000, AllowanceType.BONUS));
        list.get(5).addAllowance(new Allowance(3500, AllowanceType.MEDICAL));
        list.get(6).addAllowance(new Allowance(5500, AllowanceType.HOUSING));
        list.get(7).addAllowance(new Allowance(6500, AllowanceType.TRANSPORT));
        list.get(8).addAllowance(new Allowance(8000, AllowanceType.BONUS));
        list.get(9).addAllowance(new Allowance(4000, AllowanceType.HOUSING));
        // Deductions
        list.get(0).addDeduction(new Deduction(3000, DeductionType.TAX));
        list.get(0).addDeduction(new Deduction(1000, DeductionType.PENSION));
        list.get(1).addDeduction(new Deduction(3500, DeductionType.TAX));
        list.get(2).addDeduction(new Deduction(4000, DeductionType.TAX));
        list.get(2).addDeduction(new Deduction(1500, DeductionType.PENSION));
        list.get(3).addDeduction(new Deduction(3200, DeductionType.TAX));
        list.get(4).addDeduction(new Deduction(5000, DeductionType.TAX));
        list.get(4).addDeduction(new Deduction(2000, DeductionType.INSURANCE));
        list.get(5).addDeduction(new Deduction(3300, DeductionType.TAX));
        list.get(6).addDeduction(new Deduction(3800, DeductionType.TAX));
        list.get(7).addDeduction(new Deduction(4200, DeductionType.TAX));
        list.get(8).addDeduction(new Deduction(5500, DeductionType.TAX));
        list.get(9).addDeduction(new Deduction(2800, DeductionType.TAX));
    }

    private static List<EmployeeAccount> buildSeedAccounts(List<Employee> employees) {
        List<EmployeeAccount> accounts = new ArrayList<>();
        // Only Ali Hassan (index 0) and Sara Khan (index 1) get login accounts by default
        accounts.add(new EmployeeAccount("ali123",  "pass123", AccessRole.EMPLOYEE, employees.get(0)));
        accounts.add(new EmployeeAccount("sara456", "pass456", AccessRole.EMPLOYEE, employees.get(1)));
        return accounts;
    }
}

