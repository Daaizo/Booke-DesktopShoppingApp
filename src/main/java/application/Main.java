package application;


import dataBase.SqlConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import users.Client;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;


public class Main extends Application {
    public static ResultSet allUsersFromDatabase;
    public static HashMap<String, String> loginValues = new HashMap<>();


    private static void createHashMapWithLoginValues() {
        try {
            while (allUsersFromDatabase.next()) {
                loginValues.put(allUsersFromDatabase.getString("customerlogin"), allUsersFromDatabase.getString("customerpassword"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getUsersToHashMap(Connection con) {

        allUsersFromDatabase = Client.getUsersFromDataBase(con);
        createHashMapWithLoginValues();
    }

    public static Connection connectToDatabase() {
        SqlConnection sqlConnection = SqlConnection.createInstance();
        Connection sqlQueryConnection = sqlConnection.getConnection();
        while (sqlQueryConnection == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Connection to data base failed. Reconnect and try again.");
            alert.showAndWait();

            SqlConnection.createInstance();
            sqlQueryConnection = sqlConnection.getConnection();
        }
        return sqlQueryConnection;
    }

    @Override // MAIN METHOD that start with the app
    public void start(Stage primaryStage) {
        try {
            createWindow(primaryStage);
            Connection connection = connectToDatabase();
            getUsersToHashMap(connection);

        } catch (Exception e) {
            System.out.println(e.getMessage() + "error from start method");
        }
    }

    private void createWindow(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/application/FXML/loginGUI.fxml")));
            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
            // primaryStage.setResizable(false);
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            primaryStage.show();
        } catch (IOException e) {
            System.out.println("creating first window failed");
            e.printStackTrace();
        }
    }
}