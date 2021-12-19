package users;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Client extends User {

    public Client(String un, String ln, String n, String pas) {
        this.userName = un;
        this.name = n;
        this.lastName = ln;
        this.password = pas;

    }

    public boolean addUserToDatabase(Connection connection) {
        try {
            String query = "insert into shop.`user` (login,username,userlastname,password) values ('"
                    + this.userName + "','" + this.name + "', '" + this.lastName + "','" + this.password + "');";
            Statement stm = connection.createStatement();
            if (stm.execute(query)) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }

    @Override
    public void changeData() {

    }
}
