package controller;

import app.AppLauncher;
import enums.LeaveStatus;
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
import java.time.Month;
import java.util.Map;
import java.util.ResourceBundle;

public class SubAdminController implements Initializable {

    @FXML private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    //PAYROLL REPORT

    @FXML
    private void showPayrollReport() {
        TableView<PaySlip> table = buildTable();

        TableColumn<PaySlip, String> idCol    = col("Payslip ID", "paySlipId");
        TableColumn<PaySlip, String> empCol   = new TableColumn<>("Employee");
        TableColumn<PaySlip, String> monthCol = new TableColumn<>("Month");
        TableColumn<PaySlip, Double> grossCol = col("Gross (PKR)", "grossSalary");
        TableColumn<PaySlip, Double> netCol   = col("Net (PKR)",   "netSalary");

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

    // SALARY STATS

    @FXML
    private void showSalaryStats() {
        VBox box = new VBox(12);
        box.setStyle("-fx-padding: 20;");
        box.getChildren().add(title("Overall Salary Statistics"));

        Map<String, Double> stats = AppLauncher.data.manager.getOverallSalaryStats();
        if (stats.isEmpty()) {
            box.getChildren().add(label("No payroll data available.", "#f38ba8"));
        } else {
            box.getChildren().addAll(
                    label("Total Paid:      PKR " + fmt(stats.get("total")),   "#cdd6f4"),
                    label("Highest Salary:  PKR " + fmt(stats.get("highest")), "#a6e3a1"),
                    label("Lowest Salary:   PKR " + fmt(stats.get("lowest")),  "#f38ba8"),
                    label("Average Salary:  PKR " + fmt(stats.get("average")), "#cdd6f4")
            );
            box.getChildren().add(title("By Department"));
            for (Map.Entry<String, Double> e : AppLauncher.data.manager.getTotalPaidByDept().entrySet())
                box.getChildren().add(label(e.getKey() + ":   PKR " + fmt(e.getValue()), "#a6adc8"));
        }
        setContent(box);
    }

    //PAYSLIP HISTORY

    @FXML
    private void showPayslipHistory() {
        TableView<PaySlip> table = buildTable();

        TableColumn<PaySlip, String> idCol    = col("Payslip ID", "paySlipId");
        TableColumn<PaySlip, String> empCol   = new TableColumn<>("Employee");
        TableColumn<PaySlip, String> monthCol = new TableColumn<>("Month");
        TableColumn<PaySlip, Double> grossCol = col("Gross (PKR)", "grossSalary");
        TableColumn<PaySlip, Double> netCol   = col("Net (PKR)",   "netSalary");

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

    // LEAVE REQUESTS

    @FXML
    private void showLeaveRequests() {
        TableView<LeaveRequest> table = buildTable();

        TableColumn<LeaveRequest, Integer> idCol     = col("ID",     "requestId");
        TableColumn<LeaveRequest, String>  empCol    = new TableColumn<>("Employee");
        TableColumn<LeaveRequest, String>  startCol  = col("Start",  "startDate");
        TableColumn<LeaveRequest, String>  endCol    = col("End",    "endDate");
        TableColumn<LeaveRequest, String>  statusCol = col("Status", "leaveStatus");

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

    //ATTENDANCE REPORT

    @FXML
    private void showAttendanceReport() {
        TableView<Employee> table = buildTable();
        Month currentMonth = AppLauncher.data.manager.getMonth().getMonth();

        TableColumn<Employee, String> nameCol    = col("Name",       "name");
        TableColumn<Employee, String> deptCol    = col("Department", "department");
        TableColumn<Employee, String> presentCol = new TableColumn<>("Days Recorded");
        TableColumn<Employee, String> netCol     = new TableColumn<>("Net Salary (PKR)");

        presentCol.setCellValueFactory(d -> {
            long days = d.getValue().getAttendanceRecords().stream()
                    .filter(r -> r.getDate().getMonth() == currentMonth)
                    .count();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(days));
        });
        netCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        fmt(d.getValue().computeNetSalary())));

        table.getColumns().addAll(nameCol, deptCol, presentCol, netCol);
        table.setItems(FXCollections.observableArrayList(AppLauncher.data.allEmployees));
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

    //LOGOUT

    @FXML
    private void handleLogout() throws Exception {
        AppLauncher.loggedInSubAdmin = null;
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
        l.setStyle("-fx-text-fill: #89b4fa; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 0 0 6 0;");
        return l;
    }

    private Label label(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
        return l;
    }

    private Button actionBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1e1e2e;" +
                "-fx-background-radius: 4; -fx-padding: 3 8; -fx-cursor: hand;");
        return b;
    }

    private String fmt(double d) { return String.format("%,.2f", d); }
}
