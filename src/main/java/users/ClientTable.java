package users;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientTable {
    private SimpleIntegerProperty id;
    private SimpleStringProperty login;
    private SimpleStringProperty lastName;
    private SimpleStringProperty name;

    public ClientTable(Client c) {
        this.id = new SimpleIntegerProperty(c.getId());
        this.login = new SimpleStringProperty(c.getLogin());
        this.name = new SimpleStringProperty(c.getName());
        this.lastName = new SimpleStringProperty(c.getLastName());

    }

    public static ObservableList<ClientTable> getUsers(ResultSet users) {
        ObservableList<ClientTable> listOfUsers = FXCollections.observableArrayList();

        try {
            while (users.next()) {
                int id = users.getInt(1);
                String login = users.getString(2);
                String name = users.getString(3);
                String lastName = users.getString(4);
                String password = users.getString(5);
                String idButInString = id + "";
                listOfUsers.add(new ClientTable(new Client(id, login, name, lastName)));
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return listOfUsers;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
}



