package application;


import dataBase.MySqlConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import users.Admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class Main extends Application {
    public static ResultSet allUsersFromDatabase;
    public static HashMap<String, String> loginValues = new HashMap<>();


    private static void createHashMapWithLoginValues() {
        try {
            while (allUsersFromDatabase.next()) { // from result set to hash map
                loginValues.put(allUsersFromDatabase.getString("login"), allUsersFromDatabase.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getUsersToHashMap(Connection con) {
        Admin admin = Admin.createAdmin();
        allUsersFromDatabase = admin.getDataFromDataBase(con);
        createHashMapWithLoginValues();
    }

    private void createWindow(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/loginGUI.fxml"));
            Scene newScene = new Scene(root);
            primaryStage.setScene(newScene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("creating first window failed");
            e.printStackTrace();
        }
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

    private Connection connectToDatabase() {
        MySqlConnection sqlConnection = MySqlConnection.createInstance();
        Connection sqlQueryConnection = sqlConnection.getConnection();
        while (sqlQueryConnection == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection to data base failed. Reconnect and try again.");
            alert.showAndWait();
            MySqlConnection.createInstance();
            sqlQueryConnection = sqlConnection.getConnection();
        }
        return sqlQueryConnection;
    }
}