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

    public int getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public abstract void changeData();

    public static ResultSet getDataFromDataBase(Connection connection) {
        try {
            String query = "select * from shop.`user`"; //where user is name of user in Database
            Statement stm = connection.createStatement();
            ResultSet allUsersFromDataBase = stm.executeQuery(query);
            return allUsersFromDataBase;
        } catch (SQLException e) {
            System.out.println("error with executing SQL query");
        }
        return null;
    }
}
