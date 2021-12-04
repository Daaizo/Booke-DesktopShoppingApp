package com.example.projectshop.databaseconnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class MySqlConnection {
    private static final String url = "jdbc:mysql://localhost:3306/shop";
    private static final String user = "root";
    private static final String password = "root";
    private static MySqlConnection con ;
    private MySqlConnection()  {

    }
    public static Connection connectToDatabase(){
        if(con == null){

            try {

                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Driver loaded");

                try {
                    Connection connection = DriverManager.getConnection(url,user,password);

                    System.out.println("connection created");
                    return connection;
                } catch (SQLException e) {
                    System.out.println("connection to data base failed ");
                }

            } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Cannot find the driver in the classpath!" );
            }
        }
        else{
            System.out.println("data base connection already exists");
        }
        System.out.println("null returned");
        return null;
    }
}
