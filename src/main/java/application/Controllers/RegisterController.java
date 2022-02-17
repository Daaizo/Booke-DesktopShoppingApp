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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import users.Client;

import java.util.regex.Pattern;

public class RegisterController extends Controller {


    private final String passwordsRegex = "^(?=.*[A-Z])(?=.*[!@#$&%^*()_+])(?=.*[0-9])(?=.*[a-z]).{6,20}$";
    //Regex meaning/  at least: 1 uppercase letter,  one special sign ( basically all numbers + shift ), 1 number,1 lowercase letter, 6-20 characters
    //Rubular link : https://rubular.com/r/gEmHAEm9wKr1Tj    <- regex checker
    @FXML
    private ImageView passLengthImage, passLowercaseLetterImage, passNumberImage, passSpecialSignImage, passUppercaseLetterImage;
    @FXML
    private TextField tfLastName, tfLogin, tfPassword, tfName, tfPasswordRepeat;
    @FXML
    private Label passwordLabel, loginLabel, repeatPasswordLabel, nameLabel, lastnameLabel;
    @FXML
    private CheckBox checkbox;


    @FXML
    public void initialize() {
        prepareScene();
        setAllPasswordRequirementImages(false);
    }

    @FXML
    void goBackButtonClicked(ActionEvent event) {
        switchScene(event, loginScene);
    }


    private boolean isPasswordEmpty() {
        if (tfPassword.getText().isEmpty()) {
            displayLabelWithGivenText(passwordLabel, "Password required");
            colorField(tfPassword, passwordLabel, Color.RED);
            return true;
        } else {
            basicTheme(tfPassword, passwordLabel);

        }
        if (tfPasswordRepeat.getText().isEmpty()) {
            displayLabelWithGivenText(repeatPasswordLabel, "Password required.");
            colorField(tfPasswordRepeat, repeatPasswordLabel, Color.RED);
            return true;
        } else {
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);

        }
        return false;
    }

    private boolean passFieldMatches() {
        if (tfPassword.getText().compareTo(tfPasswordRepeat.getText()) != 0) {
            colorField(tfPassword, passwordLabel, Color.RED);
            colorField(tfPasswordRepeat, repeatPasswordLabel, Color.RED);
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
            colorField(tfLogin, loginLabel, Color.RED);
            displayLabelWithGivenText(loginLabel, "Login name required");
            return true;
        } else {
            basicTheme(tfLogin, loginLabel);
            return false;
        }
    }

    private boolean isNameEmpty() {
        if (tfName.getText().isEmpty()) {
            colorField(tfName, nameLabel, Color.RED);
            displayLabelWithGivenText(nameLabel, "Name required");
            return true;
        } else {
            basicTheme(tfName, nameLabel);
            return false;
        }
    }

    private boolean isLastNameEmpty() {
        if (tfLastName.getText().isEmpty()) {
            colorField(tfLastName, lastnameLabel, Color.RED);
            displayLabelWithGivenText(lastnameLabel, "Last name required");
            return true;
        } else {
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
        return !isLoginEmpty() && !isNameEmpty() && !isLastNameEmpty() && !isPasswordEmpty() && passFieldMatches() && checkPasswordComplexity() && isLoginUnique() && isCheckboxChecked();
    }

    private Client newUser() {
        String login = tfLogin.getText().trim();
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String password = tfPassword.getText().trim();
        return new Client(login, name, lastName, password);
    }


    @FXML
    void saveButtonClicked(ActionEvent event) {
        if (areFieldsFilledCorrectlyAndLoginIsUnique()) {
            Client newUser = newUser();
            checkConnectionWithDb();
            newUser.addUserToDatabase(getConnection());

            createAndShowAlert(Alert.AlertType.INFORMATION, "", "", "User created");
            updateHashMapWithLoginValues(newUser.getLogin(), newUser.getPassword());
            switchScene(event, loginScene);
        }
    }

    private boolean isLoginUnique() {
        String login = tfLogin.getText().trim();
        if (loginValues.containsKey(login)) {
            displayLabelWithGivenText(loginLabel, "Account with that login already exists");
            colorField(tfLogin, loginLabel, Color.RED);
            return false;
        }
        basicTheme(tfLogin, loginLabel);
        loginLabel.setVisible(false);
        return true;
    }

    private void checkPasswordRequirementAndSetProperImage(ImageView name, String regexPattern) {
        if (Pattern.matches(regexPattern, tfPassword.getText())) {
            name.setImage(new Image(absolutePathToIcons + "check.png"));
        } else {
            name.setImage(new Image(absolutePathToIcons + "no_check.png"));
        }
    }

    private void setAllPasswordRequirementImages(boolean visible) {
        if (visible) {
            passLowercaseLetterImage.setImage(new Image(absolutePathToIcons + "check.png"));
            passUppercaseLetterImage.setImage(new Image(absolutePathToIcons + "check.png"));
            passSpecialSignImage.setImage(new Image(absolutePathToIcons + "check.png"));
            passLengthImage.setImage(new Image(absolutePathToIcons + "check.png"));
            passNumberImage.setImage(new Image(absolutePathToIcons + "check.png"));
        } else {
            passLowercaseLetterImage.setImage(new Image(absolutePathToIcons + "no_check.png"));
            passUppercaseLetterImage.setImage(new Image(absolutePathToIcons + "no_check.png"));
            passSpecialSignImage.setImage(new Image(absolutePathToIcons + "no_check.png"));
            passLengthImage.setImage(new Image(absolutePathToIcons + "no_check.png"));
            passNumberImage.setImage(new Image(absolutePathToIcons + "no_check.png"));
        }
    }

    private boolean checkPasswordComplexity() {

        if (Pattern.matches(passwordsRegex, tfPassword.getText())) {
            setAllPasswordRequirementImages(true);
            return true;
        } else {
            checkPasswordRequirementAndSetProperImage(passUppercaseLetterImage, "(.*[A-Z].*)");  // if string contains at least one : // uppercase latter
            checkPasswordRequirementAndSetProperImage(passSpecialSignImage, "(.*[!@#$&%^&*()_+].*)"); // special sign
            checkPasswordRequirementAndSetProperImage(passLengthImage, "^.{6,20}$"); // 6-20 characters
            checkPasswordRequirementAndSetProperImage(passNumberImage, "(.*[0-9].*)"); // number
            checkPasswordRequirementAndSetProperImage(passLowercaseLetterImage, "(.*[a-z].*)"); //lowercase
        }
        return false;
    }


}
