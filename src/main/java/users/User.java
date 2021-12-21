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


    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public abstract void changeData();

    public ResultSet getDataFromDataBase(Connection connection){
        try{
            String query = "select * from shop.`user`"; //where user is name of user in Database
            Statement stm = connection.createStatement();
            ResultSet allUsersFromDataBase = stm.executeQuery(query);
            return  allUsersFromDataBase;
        }
        catch (SQLException e){
            System.out.println("error with executing SQL query");
        }
            return  null;
        }
}
