package app;

import auth.EmployeeAccount;
import enums.AccessRole;
import enums.EmployeeRole;
import model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DataStore — reads and writes employees + their login accounts to/from CSV files.
 *
 * Files live next to the JAR / project root:
 *   employees.csv   — one row per employee (id, name, age, dept, type, salary params, username, password)
 *
 * CSV format (no header row stored — fields by position):
 *   FULL_TIME  then id,name,age,dept,FULL_TIME,baseSalary,allowances,overtimeHrs,username,password
 *   PART_TIME  then id,name,age,dept,PART_TIME,hourlyRate,hoursWorked,username,password
 *   CONTRACT   then id,name,age,dept,CONTRACT,contractRate,contractEnd,username,password
 */
public class DataStore {

    private static final String FILE = "employees.csv";

    // SAVE

    public static void saveAll(List<Employee> employees, List<EmployeeAccount> accounts) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, false))) {
            for (Employee emp : employees) {
                // find matching account (maybe null for non-login employees)
                String username = "";
                String password = "";
                for (EmployeeAccount acc : accounts) {
                    if (acc.getEmployee() == emp) {
                        username = acc.getUsername();
                        password = acc.getRawPassword();
                        break;
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append(emp.getEmployeeId()).append(",");
                sb.append(escapeCsv(emp.getName())).append(",");
                sb.append(emp.getAge()).append(",");
                sb.append(escapeCsv(emp.getDepartment())).append(",");

                if (emp instanceof FullTimeEmployee ft) {
                    sb.append("FULL_TIME,");
                    sb.append(ft.getBaseSalary()).append(",");
                    sb.append(ft.getTotalAllowances()).append(",");
                    sb.append(ft.getOvertimeHrs());
                } else if (emp instanceof PartTimeEmployee pt) {
                    sb.append("PART_TIME,");
                    sb.append(pt.getHourlyRate()).append(",");
                    sb.append(pt.getHoursWorked());
                } else if (emp instanceof ContractEmployee ct) {
                    sb.append("CONTRACT,");
                    sb.append(ct.getContractRate()).append(",");
                    sb.append(ct.getContractEnd());
                }

                sb.append(",").append(escapeCsv(username));
                sb.append(",").append(escapeCsv(password));
                pw.println(sb);//file creation
            }
        } catch (IOException e) {
            System.err.println("[DataStore] Save failed: " + e.getMessage());
        }
    }

    // LOAD

    public static LoadResult load() {
        List<Employee> employees = new ArrayList<>();
        List<EmployeeAccount> accounts = new ArrayList<>();

        Path path = Path.of(FILE);
        if (!Files.exists(path)) return new LoadResult(employees, accounts);

        int maxId = 0; //  track highest ID seen in CSV

        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = line.split(",", -1);

                try {
                    int    id   = Integer.parseInt(f[0].trim());
                    String name = f[1].trim();
                    int    age  = Integer.parseInt(f[2].trim());
                    String dept = f[3].trim();
                    String type = f[4].trim();

                    if (id > maxId) maxId = id; // track max

                    Employee emp;
                    String username, password;

                    switch (type) {
                        case "FULL_TIME" -> {
                            double base  = Double.parseDouble(f[5].trim());
                            double allow = Double.parseDouble(f[6].trim());
                            int    ovt   = Integer.parseInt(f[7].trim());
                            username = f[8].trim();
                            password = f[9].trim();
                            //  no id passed — auto-assigned by counter
                            emp = new FullTimeEmployee(name, age, dept, base,
                                    EmployeeRole.FULL_TIME, allow, ovt);
                        }
                        case "PART_TIME" -> {
                            double rate = Double.parseDouble(f[5].trim());
                            int    hrs  = Integer.parseInt(f[6].trim());
                            username = f[7].trim();
                            password = f[8].trim();
                            emp = new PartTimeEmployee(name, age, dept, 0,
                                    EmployeeRole.PART_TIME, rate, hrs);
                        }
                        case "CONTRACT" -> {
                            double cRate = Double.parseDouble(f[5].trim());
                            LocalDate end = LocalDate.parse(f[6].trim());
                            username = f[7].trim();
                            password = f[8].trim();
                            emp = new ContractEmployee(name, age, dept, 0,
                                    EmployeeRole.CONTRACT, cRate, end);
                        }
                        default -> {
                            System.err.println("[DataStore] Unknown type: " + type);
                            continue;
                        }
                    }

                    employees.add(emp);
                    if (!username.isEmpty()) {
                        EmployeeAccount acc = new EmployeeAccount(username, password,
                                AccessRole.EMPLOYEE, emp);
                        accounts.add(acc);
                    }

                } catch (Exception ex) {
                    System.err.println("[DataStore] Skipping bad row: " + line + " → " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[DataStore] Load failed: " + e.getMessage());
        }

        //  sync static counter so next new employee gets id = maxId + 1
        Employee.syncIdCounter(maxId + 1);

        return new LoadResult(employees, accounts);
    }

    // HELPERS

    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }

    public record LoadResult(List<Employee> employees, List<EmployeeAccount> accounts) {}
}
