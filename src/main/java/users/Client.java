package users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Client extends User {
    public String id;

    public Client(String un, String ln, String n, String pas) {
        this.login = un;
        this.name = n;
        this.lastName = ln;
        this.password = pas;

    }

    public Client(String d, String un, String ln, String n, String pas) {
        this.id = d;
        this.login = un;
        this.name = n;
        this.lastName = ln;
        this.password = pas;

    }

    public static ObservableList<Client> getUsers(ResultSet users) {
        ObservableList<Client> listOfUsers = FXCollections.observableArrayList();

        try {

            while (users.next()) {
                int id = users.getInt(1);
                String login = users.getString(2);
                String firstName = users.getString(3);
                String lastName = users.getString(4);
                String password = users.getString(5);
                String idButInString = id + "";
                listOfUsers.add(new Client(idButInString, login, firstName, lastName, password));


            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return listOfUsers;
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

    public String getId() {
        return this.id;
    }

    @Override
    public void changeData() {

    }
}
