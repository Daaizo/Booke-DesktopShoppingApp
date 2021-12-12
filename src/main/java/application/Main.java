package application;


import dataBase.MySqlConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        connectToDataBase();
        createWindow(primaryStage);

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
    private void connectToDataBase(){
        MySqlConnection instance = MySqlConnection.createInstance();
    }


}