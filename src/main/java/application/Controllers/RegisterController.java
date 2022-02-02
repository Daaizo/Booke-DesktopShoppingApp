package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import users.Client;

import java.sql.Connection;

public class RegisterController extends Controller {

    @FXML
    private TextField tfLastName, tfLogin, tfPassword, tfName, tfPasswordRepeat;
    @FXML
    private Label passwordLabel, loginLabel, repeatPasswordLabel, nameLabel, lastnameLabel;
    @FXML
    private CheckBox checkbox;
    //TODO password strength 1.easy way - text 2.hard way percent  bar ?


    @FXML
    public void initialize() {
        createAnchorAndExitButton();
    }


    @FXML
    void goBackButtonClicked(ActionEvent event) {
        switchScene(event, loginScene);
    }

    private boolean isPasswordEmpty() {
        if (tfPassword.getText().isEmpty()) {
            displayLabelWithGivenText(passwordLabel, "Password required");
            colorField(tfPassword, Color.RED);
            return true;
        } else {
            basicTheme(tfPassword, passwordLabel);

        }
        if (tfPasswordRepeat.getText().isEmpty()) {
            displayLabelWithGivenText(repeatPasswordLabel, "Password required.");
            colorField(tfPasswordRepeat, Color.RED);
            return true;
        } else {
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);

        }
        return false;
    }

    private boolean passFieldMatches() {
        if (tfPassword.getText().compareTo(tfPasswordRepeat.getText()) != 0) {
            colorField(tfPassword, Color.RED);
            colorField(tfPasswordRepeat, Color.RED);
            displayLabelWithGivenText(passwordLabel, "Passwords are not identical");
            return false;
        } else {
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);
            basicTheme(tfPassword, passwordLabel);
            return true;
        }
    }

    private boolean isLoginEmpty() {
        if (tfLogin.getText().isEmpty()) {
            colorField(tfLogin, Color.RED);
            displayLabelWithGivenText(loginLabel, "Login name required");
            return true;
        } else {
            basicTheme(tfLogin, loginLabel);
            return false;
        }
    }

    private boolean isNameEmpty() {
        if (tfName.getText().isEmpty()) {
            colorField(tfName, Color.RED);
            displayLabelWithGivenText(nameLabel, "Name required");
            return true;
        } else {
            //TODO nice effect
            basicTheme(tfName, nameLabel);
            return false;
        }
    }

    private boolean isLastNameEmpty() {
        if (tfLastName.getText().isEmpty()) {
            colorField(tfLastName, Color.RED);
            displayLabelWithGivenText(lastnameLabel, "Last name required");
            return true;
        } else {
            //TODO nice effect
            basicTheme(tfLastName, lastnameLabel);
            return false;
        }
    }

    private boolean isCheckboxChecked() {
        if (checkbox.isSelected()) {
            checkbox.setEffect(new Glow());
            return true;
        } else {
            InnerShadow shadow = new InnerShadow();
            shadow.setBlurType(BlurType.ONE_PASS_BOX);
            shadow.setColor(Color.RED);
            shadow.setWidth(26);
            shadow.setHeight(36);
            shadow.setRadius(16);
            checkbox.setEffect(shadow);
            return false;
        }


    }

    private boolean areFieldsFilledCorrectlyAndLoginIsUnique() {

        return !isLoginEmpty() && !isNameEmpty() && !isLastNameEmpty() && !isPasswordEmpty() && passFieldMatches() && isLoginUnique() && isCheckboxChecked();
    }

    private Client newUser() {
        String login = tfLogin.getText().trim();
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String password = tfPassword.getText().trim();
        Client newUser = new Client(login, name, lastName, password);
        return newUser;
    }

    @FXML
    void saveButtonClicked(ActionEvent event) {
        if (areFieldsFilledCorrectlyAndLoginIsUnique()) {
            Client newUser = newUser();
            checkConnectionWithDataBaseAndDisplayError();
            Connection connection = getConnection();
            newUser.addUserToDatabase(connection);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "User created");
            alert.showAndWait();
            updateHashMapWithLoginValues(newUser.getLogin(), newUser.getPassword());
            switchScene(event, loginScene);
        }
    }

    private boolean isLoginUnique() {
        String login = tfLogin.getText().trim();
        if (loginValues.containsKey(login)) {
            displayLabelWithGivenText(loginLabel, "Account with that login exists");
            colorField(tfLogin, Color.RED);
            return false;
        }
        basicTheme(tfLogin, loginLabel);
        loginLabel.setVisible(false);
        return true;
    }

}
