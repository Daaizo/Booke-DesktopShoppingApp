module simpleApp.source {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector;


    opens application to javafx.fxml;
    exports application;
    exports application.Controllers;
    opens application.Controllers to javafx.fxml;
    exports users;
    opens users to javafx.fxml;


}