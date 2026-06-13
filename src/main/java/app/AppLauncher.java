package app;

import auth.EmployeeAccount;
import auth.SubAdminAccount;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    public static AppData        data;
    public static EmployeeAccount loggedInEmployee;
    public static SubAdminAccount loggedInSubAdmin;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/roleselect.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        //attaches UI to window
        primaryStage.setTitle("Payroll Management System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        data = Main.initData();
        launch(args);
    }
}
