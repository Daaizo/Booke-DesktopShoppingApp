package users;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Client extends User {

    public Client(String un, String ln, String n, String pas) {
        this.login = un;
        this.name = n;
        this.lastName = ln;
        this.password = pas;

    }

    public void addUserToDatabase(Connection connection) {
        try {
            String query = "insert into shop.`user` (login,username,userlastname,password) values ('"
                    + this.login + "','" + this.name + "', '" + this.lastName + "','" + this.password + "');";
            Statement stm = connection.createStatement();
            stm.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void changeData() {

    }
}
