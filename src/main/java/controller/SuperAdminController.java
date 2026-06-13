package controller;

import app.AppLauncher;
import auth.SubAdminAccount;
import enums.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable {

    @FXML private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    //  ALL EMPLOYEES

    @FXML
    private void showAllEmployees() {
        TableView<Employee> table = buildTable();

        TableColumn<Employee, Integer> idCol   = col("ID",          "employeeId");
        TableColumn<Employee, String>  nameCol = col("Name",        "name");
        TableColumn<Employee, Integer> ageCol  = col("Age",         "age");
        TableColumn<Employee, String>  deptCol = col("Department",  "department");
        TableColumn<Employee, String>  roleCol = col("Type",        "role");
        TableColumn<Employee, Double>  salCol  = col("Base Salary", "baseSalary");

        table.getColumns().addAll(idCol, nameCol, ageCol, deptCol, roleCol, salCol);
        table.setItems(FXCollections.observableArrayList(AppLauncher.data.allEmployees));
        setContent(table);
    }

    // ADD EMPLOYEE

    @FXML
    private void showAddEmployee() {
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: #1e1e2e; -fx-background: #1e1e2e;");
        scroll.setFitToWidth(true);

        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 20;");

        form.getChildren().add(title("Add New Employee"));

        // fields
        TextField nameField   = field("Full Name");
        TextField ageField    = field("Age");
        TextField deptField   = field("Department  (IT / HR / Finance)");
        TextField usernameF   = field("Login Username");
        TextField passwordF   = field("Login Password");

        // employee type
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Full Time", "Part Time", "Contract");
        typeBox.setPromptText("Employee Type");
        typeBox.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 6;");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        // type-specific fields (shown/hidden by selection)
        VBox ftFields = new VBox(8,
                label("Full-Time fields:"),
                field("Base Salary"),
                field("Allowances"),
                field("Overtime Hours"));
        ftFields.setVisible(false); ftFields.setManaged(false);

        VBox ptFields = new VBox(8,
                label("Part-Time fields:"),
                field("Hourly Rate"),
                field("Hours Worked per Month"));
        ptFields.setVisible(false); ptFields.setManaged(false);

        VBox ctFields = new VBox(8,
                label("Contract fields:"),
                field("Contract Rate (monthly)"),
                field("Contract End Date  (YYYY-MM-DD)"));
        ctFields.setVisible(false); ctFields.setManaged(false);

        typeBox.setOnAction(e -> {
            String t = typeBox.getValue();
            ftFields.setVisible("Full Time".equals(t));  ftFields.setManaged("Full Time".equals(t));
            ptFields.setVisible("Part Time".equals(t));  ptFields.setManaged("Part Time".equals(t));
            ctFields.setVisible("Contract".equals(t));   ctFields.setManaged("Contract".equals(t));
        });

        Label feedback = new Label("");
        feedback.setWrapText(true);
        feedback.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");

        Button saveBtn = new Button("Save Employee");
        saveBtn.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #1e1e2e;" +
                "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 9 18; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            try {

                String empName = nameField.getText().trim();
                int    empAge  = Integer.parseInt(ageField.getText().trim());
                String empDept = deptField.getText().trim();
                String uname   = usernameF.getText().trim();
                String upass   = passwordF.getText().trim();
                String type    = typeBox.getValue();

                if (empName.isEmpty() || empDept.isEmpty() || uname.isEmpty() || upass.isEmpty() || type == null) {
                    feedback.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
                    feedback.setText("Please fill all fields.");
                    return;
                }

                Employee newEmp;

                if ("Full Time".equals(type)) {
                    TextField[] f = fields(ftFields);
                    double base   = Double.parseDouble(f[0].getText().trim());
                    double allow  = Double.parseDouble(f[1].getText().trim());
                    int    ovt    = Integer.parseInt(f[2].getText().trim());

                    newEmp = new FullTimeEmployee(empName, empAge, empDept, base,
                            EmployeeRole.FULL_TIME, allow, ovt);

                } else if ("Part Time".equals(type)) {
                    TextField[] f = fields(ptFields);
                    double rate   = Double.parseDouble(f[0].getText().trim());
                    int    hrs    = Integer.parseInt(f[1].getText().trim());

                    newEmp = new PartTimeEmployee(empName, empAge, empDept, 0,
                            EmployeeRole.PART_TIME, rate, hrs);

                } else { // Contract
                    TextField[] f = fields(ctFields);
                    double cRate  = Double.parseDouble(f[0].getText().trim());
                    LocalDate end = LocalDate.parse(f[1].getText().trim());

                    newEmp = new ContractEmployee(empName, empAge, empDept, 0,
                            EmployeeRole.CONTRACT, cRate, end);
                }

                // add to global list
                AppLauncher.data.allEmployees.add(newEmp);

                // create employee login account
                auth.EmployeeAccount newAcc = new auth.EmployeeAccount(
                        uname, upass, AccessRole.EMPLOYEE, newEmp);
                newAcc.setPayrollManager(AppLauncher.data.manager);
                AppLauncher.data.employeeAccounts.add(newAcc);

                // PERSIST TO CSV so employee survives restart
                app.DataStore.saveAll(AppLauncher.data.allEmployees, AppLauncher.data.employeeAccounts);

                feedback.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");

                feedback.setText("✅ Employee '" + empName + "' added (ID: " + newEmp.getEmployeeId() + ") & saved permanently! Username: " + uname);

            } catch (NumberFormatException ex) {
                feedback.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
                feedback.setText("Invalid number format. Check age, salary, hours.");
            } catch (Exception ex) {
                feedback.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
                feedback.setText("Error: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
                label("Full Name:"), nameField,
                label("Age:"), ageField,
                label("Department:"), deptField,
                label("Login Username:"), usernameF,
                label("Login Password:"), passwordF,
                label("Employee Type:"), typeBox,
                ftFields, ptFields, ctFields,
                saveBtn, feedback);

        scroll.setContent(form);
        setContent(scroll);
    }

    //CREATE SUB ADMIN

    @FXML
    private void showCreateSubAdmin() {
        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 20;");
        form.getChildren().add(title("Create Sub Admin Account"));

        TextField unameField = field("Username");
        TextField passField  = field("Password");
        Label     feedback   = new Label("");
        feedback.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");

        Button createBtn = new Button("Create Sub Admin");
        createBtn.setStyle("-fx-background-color: #89dceb; -fx-text-fill: #1e1e2e;" +
                "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 9 18; -fx-cursor: hand;");

        createBtn.setOnAction(e -> {
            String u = unameField.getText().trim();
            String p = passField.getText().trim();
            if (u.isEmpty() || p.isEmpty()) {
                feedback.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
                feedback.setText("Username and password cannot be empty.");
                return;
            }
            SubAdminAccount newSA = AppLauncher.data.superAdmin.promoteToSubAdmin(
                    u, p, AppLauncher.data.manager);
            AppLauncher.data.subAdminAccounts.add(newSA);
            feedback.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");
            feedback.setText("✅ Sub Admin '" + u + "' created successfully.");
        });

        form.getChildren().addAll(
                label("Username:"), unameField,
                label("Password:"), passField,
                createBtn, feedback);

        setContent(form);
    }

    // LEAVE REQUESTS

    @FXML
    private void showLeaveRequests() {
        TableView<LeaveRequest> table = buildTable();

        TableColumn<LeaveRequest, Integer> idCol     = col("ID",       "requestId");
        TableColumn<LeaveRequest, String>  empCol    = new TableColumn<>("Employee");
        TableColumn<LeaveRequest, String>  startCol  = col("Start",    "startDate");
        TableColumn<LeaveRequest, String>  endCol    = col("End",      "endDate");
        TableColumn<LeaveRequest, String>  statusCol = col("Status",   "leaveStatus");

        empCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmployee().getName()));

        TableColumn<LeaveRequest, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = actionBtn("Approve", "#a6e3a1");
            private final Button declineBtn = actionBtn("Decline", "#f38ba8");
            private final HBox   box        = new HBox(6, approveBtn, declineBtn);

            {
                approveBtn.setOnAction(e -> {
                    LeaveRequest r = getTableView().getItems().get(getIndex());
                    r.setLeaveStatus(LeaveStatus.APPROVED);
                    getTableView().refresh();
                });
                declineBtn.setOnAction(e -> {
                    LeaveRequest r = getTableView().getItems().get(getIndex());
                    r.setLeaveStatus(LeaveStatus.REJECTED);
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, empCol, startCol, endCol, statusCol, actionCol);
        table.setItems(FXCollections.observableArrayList(AppLauncher.data.allLeaveRequests));
        setContent(table);
    }

    // SALARY OVERVIEW

    @FXML
    private void showSalaryOverview() {
        VBox box = new VBox(12);
        box.setStyle("-fx-padding: 20;");
        box.getChildren().add(title("Salary Overview — " + AppLauncher.data.manager.getMonth()));

        Map<String, Double> stats = AppLauncher.data.manager.getOverallSalaryStats();
        if (stats.isEmpty()) {
            box.getChildren().add(label2("No payroll data yet. Click 'Run Payroll' first.", "#f38ba8"));
        } else {
            box.getChildren().addAll(
                    label2("Total Paid:      PKR " + fmt(stats.get("total")),   "#cdd6f4"),
                    label2("Highest Salary:  PKR " + fmt(stats.get("highest")), "#a6e3a1"),
                    label2("Lowest Salary:   PKR " + fmt(stats.get("lowest")),  "#f38ba8"),
                    label2("Average Salary:  PKR " + fmt(stats.get("average")), "#cdd6f4")
            );
            box.getChildren().add(title("By Department"));
            for (Map.Entry<String, Double> e : AppLauncher.data.manager.getTotalPaidByDept().entrySet())
                box.getChildren().add(label2(e.getKey() + ":   PKR " + fmt(e.getValue()), "#a6adc8"));
        }
        setContent(box);
    }

    // RUN PAYROLL

    @FXML
    private void runPayroll() {
        // bump month forward so we don't create duplicate payslips for the same month
        YearMonth next = AppLauncher.data.manager.getMonth().plusMonths(1);
        PayrollManager newMgr = new PayrollManager(next, AppLauncher.data.allEmployees);
        newMgr.getPayrollHistory().addAll(AppLauncher.data.manager.getPayrollHistory()); // keep history
        // set the manager's month to next and process
        AppLauncher.data.manager = newMgr;
        AppLauncher.data.manager.processMonthly();

        // update all employee account references
        for (auth.EmployeeAccount acc : AppLauncher.data.employeeAccounts)
            acc.setPayrollManager(AppLauncher.data.manager);

        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");
        box.getChildren().addAll(
                title("Payroll Processed ✅"),
                label2("Month: " + AppLauncher.data.manager.getMonth(), "#a6e3a1"),
                label2("Payslips generated for " + AppLauncher.data.allEmployees.size() + " employees.", "#cdd6f4"),
                label2("Click 'Payslip History' to view all payslips.", "#a6adc8")
        );
        setContent(box);
    }

    // PAYSLIP HISTORY

    @FXML
    private void showPayslipHistory() {
        TableView<PaySlip> table = buildTable();

        TableColumn<PaySlip, String> idCol    = col("Payslip ID", "paySlipId");
        TableColumn<PaySlip, String> empCol   = new TableColumn<>("Employee");
        TableColumn<PaySlip, String> monthCol = new TableColumn<>("Month");
        TableColumn<PaySlip, Double> grossCol = col("Gross",      "grossSalary");
        TableColumn<PaySlip, Double> netCol   = col("Net",        "netSalary");

        empCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmployee().getName()));
        monthCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getMonth().toString()));

        table.getColumns().addAll(idCol, empCol, monthCol, grossCol, netCol);
        table.setItems(FXCollections.observableArrayList(
                AppLauncher.data.manager.getPayrollHistory()));
        setContent(table);
    }

    // DEPT PAYROLL

    @FXML
    private void showDeptPayroll() {
        TableView<Map.Entry<String, Double>> table = buildTable();

        TableColumn<Map.Entry<String, Double>, String> deptCol  = new TableColumn<>("Department");
        TableColumn<Map.Entry<String, Double>, Double> totalCol = new TableColumn<>("Total Net Paid (PKR)");

        deptCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getKey()));
        totalCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getValue()));

        table.getColumns().addAll(deptCol, totalCol);
        table.setItems(FXCollections.observableArrayList(
                AppLauncher.data.manager.getTotalPaidByDept().entrySet()));
        setContent(table);
    }

    // ATTENDANCE REPORT

    @FXML
    private void showAttendanceReport() {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");
        box.getChildren().add(title("Attendance-Based Salary Report"));

        TableView<Employee> table = buildTable();
        TableColumn<Employee, String>  nameCol    = col("Name",         "name");
        TableColumn<Employee, String>  deptCol    = col("Department",   "department");
        TableColumn<Employee, String>  presentCol = new TableColumn<>("Days Recorded");
        TableColumn<Employee, String>  netCol     = new TableColumn<>("Net Salary (PKR)");

        Month currentMonth = AppLauncher.data.manager.getMonth().getMonth();

        presentCol.setCellValueFactory(d -> {
            long days = d.getValue().getAttendanceRecords().stream()
                    .filter(r -> r.getDate().getMonth() == currentMonth)
                    .count();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(days));
        });
        netCol.setCellValueFactory(d -> {
            double net = d.getValue().computeNetSalary();
            return new javafx.beans.property.SimpleStringProperty(fmt(net));
        });

        table.getColumns().addAll(nameCol, deptCol, presentCol, netCol);
        table.setItems(FXCollections.observableArrayList(AppLauncher.data.allEmployees));
        setContent(table);
    }

    // LOGOUT

    @FXML
    private void handleLogout() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/roleselect.fxml"));
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    // HELPERS

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    private <T> TableView<T> buildTable() {
        TableView<T> t = new TableView<>();
        t.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;");
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return t;
    }

    @SuppressWarnings("unchecked")
    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    private Label title(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 0 0 6 0;");
        return l;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");
        return l;
    }

    private Label label2(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
        return l;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;" +
                "-fx-prompt-text-fill: #585b70; -fx-border-color: #45475a;" +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        return tf;
    }

    private Button actionBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1e1e2e;" +
                "-fx-background-radius: 4; -fx-padding: 3 8; -fx-cursor: hand;");
        return b;
    }

    /** Extract TextFields from a VBox (skips Label children). */
    private TextField[] fields(VBox vbox) {
        return vbox.getChildren().stream()
                .filter(n -> n instanceof TextField)
                .map(n -> (TextField) n)
                .toArray(TextField[]::new);
    }

    private String fmt(double d) {
        return String.format("%,.2f", d);
    }
}
