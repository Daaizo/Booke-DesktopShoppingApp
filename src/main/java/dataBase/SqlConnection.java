package dataBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//TODO button that closes app when connection to db fails

public class SqlConnection {
    private static final String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String user = "daaizo";
    private static final String password = "admin";
    private static SqlConnection instance = null;
    private Connection connection = null;

    private SqlConnection() {
        loadJdbcDriver();
        connectToDatabase();
    }

    public static SqlConnection createInstance() {
        if (instance == null) {
            try {
                instance = new SqlConnection();
                return instance;
            } catch (Exception e) {
                System.out.println(e.getMessage() + "problem with creating instance of connection");
            }
        } else {
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

