package application;


import dataBase.SqlConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;
import java.util.Optional;


public class Main extends Application {
    public static Connection connectToDatabase() {
        SqlConnection sqlConnection = SqlConnection.createInstance();
        Connection sqlQueryConnection = sqlConnection.getConnection();
        while (sqlQueryConnection == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection to data base failed. Reconnect and try again.", ButtonType.OK, ButtonType.CLOSE);
            Optional<ButtonType> alertButton = alert.showAndWait();
            if (alertButton.isPresent() && alertButton.get() == ButtonType.CLOSE) {
                Platform.exit();
                break;
            }
            SqlConnection.createInstance();
            sqlQueryConnection = sqlConnection.getConnection();
        }
        return sqlQueryConnection;
    }

    @Override // MAIN METHOD that start with the app
    public void start(Stage primaryStage) {
        try {
            createWindow(primaryStage);
            connectToDatabase();

        } catch (Exception e) {
            System.out.println(e.getMessage() + "error from start method");
        }
    }

    private void createWindow(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/application/FXML/loginGUI.fxml")));
            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            primaryStage.show();
        } catch (IOException e) {
            System.out.println("creating first window failed");
            e.printStackTrace();
        }
    }
}