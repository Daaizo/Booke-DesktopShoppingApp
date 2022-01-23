package dataBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class MySqlConnection {
    private static final String url = "jdbc:mysql://localhost:3306/shop";
    private static final String user = "root";
    private static final String password = "root";
    private static MySqlConnection instance = null;
    private Connection connection = null;

    private MySqlConnection() {
        loadJdbcDriver();
        connectToDatabase();
    }

    public static MySqlConnection createInstance(){
        if (instance == null) {
            try {
                instance = new MySqlConnection();
                return instance;
            } catch (Exception e) {
                System.out.println(e.getMessage() + "problem with creating instance of connection");
            }
        }
        else {
            System.out.println("data base connection already exists //trying to reconnect ");
            instance.connectToDatabase();
        }
        return  instance;
    }

    private void connectToDatabase() {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e) {
            System.out.println("connection to data base failed ");
            }
    }

    private void loadJdbcDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!");
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

}

