package controller;

import app.AppLauncher;
import auth.EmployeeAccount;
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
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML private Label      welcomeLabel;
    @FXML private StackPane  contentArea;

    private EmployeeAccount currentAccount;
    private Employee        currentEmployee;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentAccount  = AppLauncher.loggedInEmployee;
        currentEmployee = currentAccount.getEmployee();
        welcomeLabel.setText("Welcome, " + currentEmployee.getName());
    }

    // MY PAYSLIP

    @FXML
    private void showPayslip() {
        List<PaySlip> all = AppLauncher.data.manager.getPayrollHistory();

        // collect all payslips for this employee
        List<PaySlip> mine = all.stream()
                .filter(p -> p.getEmployee().equals(currentEmployee))
                .toList();

        if (mine.isEmpty()) {
            VBox box = vbox();
            box.getChildren().addAll(
                    title("My Payslips"),
                    label("No payslip generated yet. Contact admin.", "#f38ba8"));
            setContent(box);
            return;
        }

        // show most recent at top in a table
        TableView<PaySlip> table = buildTable();

        TableColumn<PaySlip, String> idCol    = col("Payslip ID", "paySlipId");
        TableColumn<PaySlip, String> monthCol = new TableColumn<>("Month");
        TableColumn<PaySlip, Double> grossCol = col("Gross (PKR)", "grossSalary");
        TableColumn<PaySlip, Double> netCol   = col("Net (PKR)",   "netSalary");

        monthCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getMonth().toString()));

        table.getColumns().addAll(idCol, monthCol, grossCol, netCol);
        table.setItems(FXCollections.observableArrayList(mine));

        VBox box = vbox();
        box.getChildren().addAll(title("My Payslips"), table);
        setContent(box);
    }

    // MY ATTENDANCE

    @FXML
    private void showAttendance() {
        TableView<AttendanceRecord> table = buildTable();

        TableColumn<AttendanceRecord, String> dateCol   = col("Date",         "date");
        TableColumn<AttendanceRecord, String> statusCol = col("Status",       "attendanceStatus");
        TableColumn<AttendanceRecord, Double> hoursCol  = col("Hours Worked", "hourWorked");

        table.getColumns().addAll(dateCol, statusCol, hoursCol);
        table.setItems(FXCollections.observableArrayList(
                currentEmployee.getAttendanceRecords()));

        VBox box = vbox();
        box.getChildren().addAll(title("My Attendance"), table);
        setContent(box);
    }

    //REQUEST LEAVE

    @FXML
    private void showLeaveForm() {
        VBox form = vbox();
        form.getChildren().add(title("Submit Leave Request"));

        DatePicker startPicker = new DatePicker();
        DatePicker endPicker   = new DatePicker();
        startPicker.setPromptText("Start Date");
        endPicker.setPromptText("End Date");

        Label feedback = new Label("");
        feedback.setWrapText(true);
        feedback.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");

        Button submitBtn = new Button("Submit Leave Request");
        submitBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #1e1e2e;" +
                "-fx-font-weight: bold; -fx-background-radius: 6;" +
                "-fx-padding: 9 18; -fx-cursor: hand;");

        submitBtn.setOnAction(e -> {
            LocalDate start = startPicker.getValue();
            LocalDate end   = endPicker.getValue();

            //date validation (the bug fix)
            if (start == null || end == null) {
                err(feedback, "Please select both start and end dates.");
                return;
            }
            if (start.isBefore(LocalDate.now())) {
                err(feedback, "Start date cannot be in the past.");
                return;
            }
            if (!end.isAfter(start)) {
                err(feedback, "End date must be after start date.");
                return;
            }

            int id = currentEmployee.getLeaveRequests().size() + 1;
            currentAccount.submitLeaveRequest(id, start, end);

            // also add to global list so admins can see it
            LeaveRequest lr = currentEmployee.getLeaveRequests()
                    .get(currentEmployee.getLeaveRequests().size() - 1);
            AppLauncher.data.allLeaveRequests.add(lr);

            feedback.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 12px;");
            feedback.setText(" Leave request submitted from " + start + " to " + end + ".");
        });

        form.getChildren().addAll(
                label("Start Date:", "#a6adc8"), startPicker,
                label("End Date:",   "#a6adc8"), endPicker,
                submitBtn, feedback);

        setContent(form);
    }

    // MY LEAVE HISTORY

    @FXML
    private void showLeaveHistory() {
        TableView<LeaveRequest> table = buildTable();

        TableColumn<LeaveRequest, Integer> idCol     = col("ID",     "requestId");
        TableColumn<LeaveRequest, String>  startCol  = col("Start",  "startDate");
        TableColumn<LeaveRequest, String>  endCol    = col("End",    "endDate");
        TableColumn<LeaveRequest, String>  statusCol = col("Status", "leaveStatus");

        table.getColumns().addAll(idCol, startCol, endCol, statusCol);
        table.setItems(FXCollections.observableArrayList(
                currentEmployee.getLeaveRequests()));

        VBox box = vbox();
        box.getChildren().addAll(title("My Leave History"), table);
        setContent(box);
    }

    // MY REPORT

    @FXML
    private void showMyReport() {
        VBox box = vbox();
        box.getChildren().add(title("My Salary Report"));

        box.getChildren().addAll(
                label("Name:          " + currentEmployee.getName(),       "#cdd6f4"),
                label("Department:    " + currentEmployee.getDepartment(), "#cdd6f4"),
                label("Role:          " + currentEmployee.getRole(),       "#cdd6f4"),
                label("Gross Salary:  PKR " + fmt(currentEmployee.calculateSalary()),  "#a6e3a1"),
                label("Net Salary:    PKR " + fmt(currentEmployee.computeNetSalary()), "#a6e3a1")
        );

        if (!currentEmployee.getAllowances().isEmpty()) {
            box.getChildren().add(title("Allowances"));
            for (Allowance a : currentEmployee.getAllowances())
                box.getChildren().add(label("+ " + a.getAllowanceType() + ":  PKR " + fmt(a.getAmount()), "#a6adc8"));
        }

        if (!currentEmployee.getDeductions().isEmpty()) {
            box.getChildren().add(title("Deductions"));
            for (Deduction d : currentEmployee.getDeductions())
                box.getChildren().add(label("- " + d.getDeductionType() + ":  PKR " + fmt(d.getDeduction()), "#f38ba8"));
        }

        setContent(box);
    }

    // SALARY CALCULATOR

    @FXML
    private void showSalaryCalculator() {
        VBox form = vbox();
        form.getChildren().add(title("💰 Salary Calculator"));

        Label intro = label("Enter values below to compute your estimated salary — these don't change your actual record.", "#a6adc8");
        intro.setWrapText(true);

        // Role selector
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Full Time", "Part Time", "Contract");
        typeBox.setPromptText("Select Employee Type");
        typeBox.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 6;");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        // Prefill with current employee type
        String empType = currentEmployee.getRole().name();
        if (empType.equals("FULL_TIME"))  typeBox.setValue("Full Time");
        else if (empType.equals("PART_TIME")) typeBox.setValue("Part Time");
        else typeBox.setValue("Contract");

        // Full-Time fields
        TextField ftBase  = field("Base Salary (PKR)");
        TextField ftAllow = field("Total Allowances (PKR)");
        TextField ftOvt   = field("Overtime Hours");
        TextField ftTax   = field("Tax Deduction (PKR)");
        TextField ftOther = field("Other Deductions (PKR)  — 0 if none");
        VBox ftBox = new VBox(8, label("Full-Time inputs:", "#585b70"), ftBase, ftAllow, ftOvt, ftTax, ftOther);

        // Part-Time fields
        TextField ptRate = field("Hourly Rate (PKR)");
        TextField ptHrs  = field("Hours Worked");
        TextField ptTax  = field("Tax Deduction (PKR)");
        VBox ptBox = new VBox(8, label("Part-Time inputs:", "#585b70"), ptRate, ptHrs, ptTax);

        // Contract fields
        TextField ctRate = field("Monthly Contract Rate (PKR)");
        TextField ctTax  = field("Tax Deduction (PKR)");
        VBox ctBox = new VBox(8, label("Contract inputs:", "#585b70"), ctRate, ctTax);

        // Prefill current employee values
        if (currentEmployee instanceof model.FullTimeEmployee ft) {
            ftBase.setText(String.valueOf(ft.getBaseSalary()));
            ftAllow.setText(String.valueOf(ft.getTotalAllowances()));
            ftOvt.setText(String.valueOf(ft.getOvertimeHrs()));
        } else if (currentEmployee instanceof model.PartTimeEmployee pt) {
            ptRate.setText(String.valueOf(pt.getHourlyRate()));
            ptHrs.setText(String.valueOf(pt.getHoursWorked()));
        } else if (currentEmployee instanceof model.ContractEmployee ct) {
            ctRate.setText(String.valueOf(ct.getContractRate()));
        }

        // Show/hide logic
        Runnable updateVisibility = () -> {
            String t = typeBox.getValue();
            ftBox.setVisible("Full Time".equals(t)); ftBox.setManaged("Full Time".equals(t));
            ptBox.setVisible("Part Time".equals(t)); ptBox.setManaged("Part Time".equals(t));
            ctBox.setVisible("Contract".equals(t));  ctBox.setManaged("Contract".equals(t));
        };
        typeBox.setOnAction(e -> updateVisibility.run());
        updateVisibility.run();

        // Result area
        VBox resultBox = new VBox(6);
        resultBox.setStyle("-fx-background-color: #181825; -fx-padding: 14; -fx-background-radius: 8;");
        resultBox.setVisible(false);

        Button calcBtn = new Button("Calculate Salary");
        calcBtn.setStyle("-fx-background-color: #cba6f7; -fx-text-fill: #1e1e2e;" +
                "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 9 18; -fx-cursor: hand;");

        Label errLabel = label("", "#f38ba8");

        calcBtn.setOnAction(e -> {
            errLabel.setText("");
            resultBox.setVisible(false);
            resultBox.getChildren().clear();
            try {
                double gross = 0, net = 0, totalDed = 0;
                String t = typeBox.getValue();
                if (t == null) { errLabel.setText("Select an employee type."); return; }

                if ("Full Time".equals(t)) {
                    double base  = parseOrZero(ftBase.getText());
                    double allow = parseOrZero(ftAllow.getText());
                    int    ovt   = (int) parseOrZero(ftOvt.getText());
                    double tax   = parseOrZero(ftTax.getText());
                    double other = parseOrZero(ftOther.getText());
                    gross    = base + allow + (2000 * ovt);
                    totalDed = tax + other;
                    net      = Math.max(0, gross - totalDed);

                    resultBox.getChildren().addAll(
                        resultTitle("Calculation Breakdown"),
                        resultRow("Base Salary",    fmt(base)),
                        resultRow("Allowances",     fmt(allow)),
                        resultRow("Overtime Pay (" + ovt + " hrs × PKR 2,000)", fmt(2000.0 * ovt)),
                        resultSep(),
                        resultRow("Gross Salary",   fmt(gross)),
                        resultRow("Tax Deduction",  "− " + fmt(tax)),
                        resultRow("Other Deductions", "− " + fmt(other)),
                        resultSep(),
                        resultHighlight("Net Salary", fmt(net))
                    );

                } else if ("Part Time".equals(t)) {
                    double rate = parseOrZero(ptRate.getText());
                    int    hrs  = (int) parseOrZero(ptHrs.getText());
                    double tax  = parseOrZero(ptTax.getText());
                    gross    = rate * hrs;
                    totalDed = tax;
                    net      = Math.max(0, gross - totalDed);

                    resultBox.getChildren().addAll(
                        resultTitle("Calculation Breakdown"),
                        resultRow("Hourly Rate × Hours", fmt(rate) + " × " + hrs + " = " + fmt(gross)),
                        resultSep(),
                        resultRow("Gross Salary",   fmt(gross)),
                        resultRow("Tax Deduction",  "− " + fmt(tax)),
                        resultSep(),
                        resultHighlight("Net Salary", fmt(net))
                    );

                } else { // Contract
                    double rate = parseOrZero(ctRate.getText());
                    double tax  = parseOrZero(ctTax.getText());
                    gross    = rate;
                    totalDed = tax;
                    net      = Math.max(0, gross - totalDed);

                    resultBox.getChildren().addAll(
                        resultTitle("Calculation Breakdown"),
                        resultRow("Contract Rate (monthly)", fmt(rate)),
                        resultSep(),
                        resultRow("Gross Salary",  fmt(gross)),
                        resultRow("Tax Deduction", "− " + fmt(tax)),
                        resultSep(),
                        resultHighlight("Net Salary", fmt(net))
                    );
                }

                resultBox.setVisible(true);

            } catch (Exception ex) {
                errLabel.setText("Check your inputs — make sure all numbers are valid.");
            }
        });

        form.getChildren().addAll(intro, label("Employee Type:", "#a6adc8"), typeBox,
                ftBox, ptBox, ctBox, calcBtn, errLabel, resultBox);
        setContent(new javafx.scene.control.ScrollPane(form) {{
            setStyle("-fx-background-color: #1e1e2e; -fx-background: #1e1e2e;");
            setFitToWidth(true);
        }});
    }

    // Calculator helper labels
    private javafx.scene.layout.HBox resultRow(String label, String value) {
        Label l = new Label(label); l.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");
        Label v = new Label(value); v.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 12px; -fx-font-weight: bold;");
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
        row.setStyle("-fx-spacing: 8;");
        l.setMinWidth(260);
        row.getChildren().addAll(l, v);
        return row;
    }
    private Label resultTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 0 0 4 0;");
        return l;
    }
    private javafx.scene.layout.Region resultSep() {
        javafx.scene.layout.Region r = new javafx.scene.layout.Region();
        r.setMinHeight(1); r.setMaxHeight(1);
        r.setStyle("-fx-background-color: #45475a;");
        r.setPrefWidth(Double.MAX_VALUE);
        return r;
    }
    private javafx.scene.layout.HBox resultHighlight(String label, String value) {
        Label l = new Label(label); l.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label v = new Label("PKR " + value); v.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 14px; -fx-font-weight: bold;");
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
        l.setMinWidth(260);
        row.getChildren().addAll(l, v);
        return row;
    }
    private double parseOrZero(String s) {
        try {
            double val = Double.parseDouble(s.trim());
            return Math.max(0, val);   // clamp — no negatives allowed
        } catch (Exception e) { return 0; }
    }

    @FXML
    private void generateReport() {
        VBox box = vbox();
        box.getChildren().add(title("📄 My Full Report"));

        Employee e = currentEmployee;
        box.getChildren().addAll(
                label("Employee ID:   " + e.getEmployeeId(),   "#a6adc8"),
                label("Name:          " + e.getName(),          "#cdd6f4"),
                label("Age:           " + e.getAge(),           "#cdd6f4"),
                label("Department:    " + e.getDepartment(),    "#cdd6f4"),
                label("Type:          " + e.getRole(),          "#cdd6f4")
        );

        box.getChildren().add(title("💰 Salary"));
        box.getChildren().addAll(
                label("Gross Salary:  PKR " + fmt(e.calculateSalary()),  "#a6e3a1"),
                label("Net Salary:    PKR " + fmt(e.computeNetSalary()), "#a6e3a1")
        );

        if (!e.getAllowances().isEmpty()) {
            box.getChildren().add(title("Allowances"));
            for (model.Allowance a : e.getAllowances())
                box.getChildren().add(label("  + " + a.getAllowanceType() + ":  PKR " + fmt(a.getAmount()), "#a6adc8"));
        }

        if (!e.getDeductions().isEmpty()) {
            box.getChildren().add(title("Deductions"));
            for (model.Deduction d : e.getDeductions())
                box.getChildren().add(label("  − " + d.getDeductionType() + ":  PKR " + fmt(d.getDeduction()), "#f38ba8"));
        }

        if (!e.getAttendanceRecords().isEmpty()) {
            box.getChildren().add(title("Attendance Summary"));
            long present = e.getAttendanceRecords().stream()
                    .filter(r -> r.getAttendanceStatus().toString().equals("PRESENT")).count();
            long late    = e.getAttendanceRecords().stream()
                    .filter(r -> r.getAttendanceStatus().toString().equals("LATE")).count();
            long absent  = e.getAttendanceRecords().stream()
                    .filter(r -> r.getAttendanceStatus().toString().equals("ABSENT")).count();
            box.getChildren().addAll(
                    label("  Present: " + present, "#a6e3a1"),
                    label("  Late:    " + late,    "#f9e2af"),
                    label("  Absent:  " + absent,  "#f38ba8")
            );
        }

        if (!e.getLeaveRequests().isEmpty()) {
            box.getChildren().add(title("Leave Requests"));
            for (model.LeaveRequest lr : e.getLeaveRequests())
                box.getChildren().add(label(
                        "  " + lr.getStartDate() + " → " + lr.getEndDate() + "  [" + lr.getLeaveStatus() + "]",
                        "#a6adc8"));
        }

        setContent(box);
    }

    //LOGOUT

    @FXML
    private void handleLogout() throws Exception {
        AppLauncher.loggedInEmployee = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/roleselect.fxml"));
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    // HELPERS
    private void setContent(Node node) { contentArea.getChildren().setAll(node); }

    private VBox vbox() {
        VBox b = new VBox(10);
        b.setStyle("-fx-padding: 10;");
        return b;
    }

    private <T> TableView<T> buildTable() {
        TableView<T> t = new TableView<>();
        t.setStyle("-fx-background-color: #313244;");
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
        l.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 0 0 4 0;");
        return l;
    }

    private Label label(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
        return l;
    }

    private void err(Label l, String msg) {
        l.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
        l.setText(msg);
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;" +
                "-fx-prompt-text-fill: #585b70; -fx-border-color: #45475a;" +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        return tf;
    }

    private String fmt(double d) { return String.format("%,.2f", d); }
}
