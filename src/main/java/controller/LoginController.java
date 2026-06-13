package controller;

import app.AppLauncher;
import auth.EmployeeAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public enum Mode { SUPER_ADMIN, SUB_ADMIN, EMPLOYEE }

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Label         roleHintLabel;

    private Mode mode = Mode.EMPLOYEE;

    /** Called by the previous screen before this scene is shown. */
    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case SUPER_ADMIN -> roleHintLabel.setText("Signing in as  👑 Super Admin");
            case SUB_ADMIN   -> roleHintLabel.setText("Signing in as  📋 Sub Admin");
            case EMPLOYEE    -> roleHintLabel.setText("Signing in as  👤 Employee");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();

        try {
            switch (mode) {

                case SUPER_ADMIN -> {
                    if (AppLauncher.data.superAdmin.login(user, pass)) {
                        loadScene("superadmin.fxml");
                    } else {
                        errorLabel.setText("Invalid Super Admin credentials.");
                    }
                }

                case SUB_ADMIN -> {
                    // check the dynamically-grown subAdmins list
                    boolean found = false;
                    for (auth.SubAdminAccount sa : AppLauncher.data.subAdminAccounts) {
                        if (sa.login(user, pass)) {
                            AppLauncher.loggedInSubAdmin = sa;
                            loadScene("subadmin.fxml");
                            found = true;
                            break;
                        }
                    }
                    if (!found) errorLabel.setText("Invalid Sub Admin credentials.");
                }

                case EMPLOYEE -> {
                    boolean found = false;
                    for (EmployeeAccount acc : AppLauncher.data.employeeAccounts) {
                        if (acc.login(user, pass)) {
                            AppLauncher.loggedInEmployee = acc;
                            loadScene("employee.fxml");
                            found = true;
                            break;
                        }
                    }
                    if (!found) errorLabel.setText("Invalid employee credentials.");
                }
            }
        } catch (Exception e) {
            errorLabel.setText("Something went wrong.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            String target = (mode == Mode.EMPLOYEE) ? "roleselect.fxml" : "adminselect.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + target));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadScene(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}
