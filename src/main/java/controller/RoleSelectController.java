package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RoleSelectController {

    @FXML
    private void selectAdmin(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminselect.fxml"));
            Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    private void selectEmployee(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController ctrl = loader.getController();
            ctrl.setMode(LoginController.Mode.EMPLOYEE);
            Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
