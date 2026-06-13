module com.example.payrollmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    opens app;
    opens controller;
    opens model;
    opens auth;
    opens enums;
}