package users;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

abstract class User {
    protected String login;
    protected String password;
    protected String name;
    protected String lastName;
    protected Integer id;


    public String getLogin() {
        return this.login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static ResultSet getUsersFromDataBase(Connection connection) {
        try {
            String query = "select * from DAAIZO.CUSTOMER";
            Statement stm = connection.createStatement();
            return stm.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("error with executing SQL query");
        }
        return null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
