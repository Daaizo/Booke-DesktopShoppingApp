package dataBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class MySqlConnection {
    private static final String url = "jdbc:mysql://localhost:3306/shop";
    private static final String user = "root";
    private static final String password = "root";
    private static MySqlConnection instance = null;
    public Connection connection = null;

    private MySqlConnection() {
        loadJdbcDriver();
        connectToDatabase();
    }

    public static MySqlConnection createInstance(){
        if (instance == null) {
            try {
                MySqlConnection con = new MySqlConnection();
                return con;
            } catch (Exception e) {
                System.out.println(e.getMessage() + "problem with creating instance of connection");
            }
        }
        else {
                System.out.println("data base connection already exists");
            }
        return  null;
    }

    private void connectToDatabase() {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("connection to data base success");
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
}

