package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminSelectController {

    @FXML
    private void selectSuperAdmin(ActionEvent e) {
        navigate(e, LoginController.Mode.SUPER_ADMIN);
    }

    @FXML
    private void selectSubAdmin(ActionEvent e) {
        navigate(e, LoginController.Mode.SUB_ADMIN);
    }

    @FXML
    private void goBack(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/roleselect.fxml"));
            Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void navigate(ActionEvent e, LoginController.Mode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController ctrl = loader.getController();
            ctrl.setMode(mode);
            Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
