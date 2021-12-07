package simpleApp.source;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ShopApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader mainScene = new FXMLLoader(ShopApplication.class.getResource("loginGUI.fxml"));
        Scene login = new Scene(mainScene.load());
        primaryStage.setTitle("Login");
        primaryStage.setScene(login);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try{
            launch();
        }
        catch (Exception e){

            e.printStackTrace();
        }
    }
}