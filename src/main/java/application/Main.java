package application;


import dataBase.MySqlConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import users.Admin;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class Main extends Application{
    public static ResultSet allUsersFromDatabase;
    public static HashMap<String,String> loginValues = new HashMap<>();

    @Override // MAIN METHOD that start with the app
    public void start(Stage primaryStage){
        try {
            createWindow(primaryStage);
            Connection connection = connectToDataBase();

            Admin admin = Admin.createAdmin();
            allUsersFromDatabase = admin.getDataFromDataBase(connection);
            createHashMapWithLoginValues();


        }catch (Exception e){
            System.out.println(e.getMessage() + "error from start method");
        }
    }

    private void createHashMapWithLoginValues() {
        try {
            while(allUsersFromDatabase.next()){ // from result set to hash map
                loginValues.put(allUsersFromDatabase.getString("username"), allUsersFromDatabase.getString("password"));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private void createWindow(Stage primaryStage){
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
    private Connection connectToDataBase(){
        MySqlConnection instance = MySqlConnection.createInstance();
        return  instance.connection;
    }


}