package application.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import users.Client;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterController extends Controller {


    @FXML
    private ImageView passLengthImage, passLowercaseLetterImage, passNumberImage, passSpecialSignImage, passUppercaseLetterImage;
    @FXML
    private TextField tfLastName, tfLogin, tfPassword, tfName, tfPasswordRepeat;
    @FXML
    private Label passwordLabel, loginLabel, repeatPasswordLabel, nameLabel, lastnameLabel;
    @FXML
    private CheckBox checkbox;
    @FXML
    private Button showPasswordButton, showRepeatPasswordButton;
    private ChangeListener<String> passwordFieldListener;

    @FXML
    private void initialize() {
        prepareSceneWithoutLogoutButton();
        createButtons();
        setTextFieldListeners();
        preparePasswordFields();
    }


    @FXML
    private void registerButtonClicked(ActionEvent event) {

        try {
            if (areFieldsFilledCorrectlyAndLoginIsUnique()) {
                Client newUser = newUser();
                newUser.addUserToDatabase(getConnection());
                createAndShowAlert(Alert.AlertType.INFORMATION, "Your account has been successfully created", "Success", "");
                switchScene(event, loginScene);
            } else {
                anchor.requestFocus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goBackButtonClicked(ActionEvent event) {
        switchScene(event, loginScene);
    }

    private boolean isPasswordEmpty() {
        if (tfPassword.getText().isEmpty()) {
            displayLabelWithGivenText(passwordLabel, "Password required");
            makeFieldsBorderRed(tfPassword, passwordLabel);
            return true;
        } else {
            basicTheme(tfPassword, passwordLabel);

        }
        if (tfPasswordRepeat.getText().isEmpty()) {
            displayLabelWithGivenText(repeatPasswordLabel, "Password required.");
            makeFieldsBorderRed(tfPasswordRepeat, repeatPasswordLabel);
            return true;
        } else {
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);

        }
        return false;
    }


    private void createButtons() {
        createGoBackButton(event -> switchScene(event, loginScene));
    }

    private void preparePasswordFields() {
        setAllPasswordRequirementImages(false);
        setPasswordVisibilityButtons();
        passwordFieldListener = passwordFieldListener();
        tfPassword.textProperty().addListener(passwordFieldListener);
    }

    private void setTextFieldListeners() {
        tfLogin.textProperty().addListener((observableValue, s, t1) -> basicTheme(tfLogin, loginLabel));
        tfName.textProperty().addListener((observableValue, s, t1) -> basicTheme(tfName, nameLabel));
        tfLastName.textProperty().addListener((observableValue, s, t1) -> basicTheme(tfLastName, lastnameLabel));
        tfPasswordRepeat.textProperty().addListener((observableValue, s, t1) -> {
            basicTheme(tfPassword, passwordLabel);
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);
        });
    }

    private ChangeListener<String> passwordFieldListener() {
        return (observableValue, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                if (checkPasswordRegex(newValue)) {
                    setAllPasswordRequirementImages(true);
                } else {
                    checkPasswordRequirementAndSetProperImage(passUppercaseLetterImage, "(.*[A-Z].*)");  // if string contains at least one : // uppercase latter
                    checkPasswordRequirementAndSetProperImage(passSpecialSignImage, "(.*[!@#$&%^&*()._+].*)"); // special sign
                    checkPasswordRequirementAndSetProperImage(passLengthImage, "^.{6,20}$"); // 6-20 characters
                    checkPasswordRequirementAndSetProperImage(passNumberImage, "(.*[0-9].*)"); // number
                    checkPasswordRequirementAndSetProperImage(passLowercaseLetterImage, "(.*[a-z].*)"); //lowercase
                }
            } else {
                setAllPasswordRequirementImages(false);
            }
            basicTheme(tfPassword, passwordLabel);
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);
        };
    }

    private void setPasswordVisibilityButtons() {
        String hiddenPassIconName = "hiddenPassword";
        String showPassIconName = "showPassword";
        String iconsFolder = "PasswordIcons";
        showPasswordButton.setGraphic(setImageFromIconsFolder(iconsFolder, hiddenPassIconName));
        showRepeatPasswordButton.setGraphic(setImageFromIconsFolder(iconsFolder, hiddenPassIconName));
        showPasswordButton.setBackground(Background.EMPTY);
        showRepeatPasswordButton.setBackground(Background.EMPTY);
        showPasswordButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            tfPassword.textProperty().removeListener(passwordFieldListener);
            showPasswordButtonPressed(tfPassword, showPasswordButton, setImageFromIconsFolder(iconsFolder, hiddenPassIconName));
        });

        showPasswordButton.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            tfPassword.textProperty().addListener(passwordFieldListener);
            showPasswordButtonReleased(tfPassword, showPasswordButton, setImageFromIconsFolder(iconsFolder, hiddenPassIconName));
        });
        showRepeatPasswordButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> showPasswordButtonPressed(tfPasswordRepeat, showRepeatPasswordButton, setImageFromIconsFolder(iconsFolder, showPassIconName)));
        showRepeatPasswordButton.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> showPasswordButtonReleased(tfPasswordRepeat, showRepeatPasswordButton, setImageFromIconsFolder(iconsFolder, hiddenPassIconName)));
    }





    private boolean passFieldMatches() {
        if (tfPassword.getText().compareTo(tfPasswordRepeat.getText()) != 0) {
            makeFieldsBorderRed(tfPassword, passwordLabel);
            makeFieldsBorderRed(tfPasswordRepeat, repeatPasswordLabel);
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
            makeFieldsBorderRed(tfLogin, loginLabel);
            displayLabelWithGivenText(loginLabel, "Login name required");
            return true;
        } else {
            basicTheme(tfLogin, loginLabel);
            return false;
        }
    }

    private boolean isNameEmpty() {
        if (tfName.getText().isEmpty()) {
            makeFieldsBorderRed(tfName, nameLabel);
            displayLabelWithGivenText(nameLabel, "Name required");
            return true;
        } else {
            basicTheme(tfName, nameLabel);
            return false;
        }
    }

    private boolean isLastNameEmpty() {
        if (tfLastName.getText().isEmpty()) {
            makeFieldsBorderRed(tfLastName, lastnameLabel);
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

    private boolean areFieldsFilledCorrectlyAndLoginIsUnique() throws SQLException {
        return !isLoginEmpty() && !isNameEmpty() && !isLastNameEmpty() && !isPasswordEmpty() && checkPasswordComplexity() && passFieldMatches() && isLoginUnique()
                && isCheckboxChecked() && checkRegex(tfLogin.getText(), tfName.getText(), tfLastName.getText());
    }

    private Client newUser() {
        String login = tfLogin.getText();
        String name = tfName.getText();
        String lastName = tfLastName.getText();
        String password = tfPassword.getText();
        return new Client(login, name, lastName, password);
    }



    private boolean isLoginUnique() throws SQLException {
        checkConnectionWithDb();
        String login = tfLogin.getText().trim();
        if (Client.isClientInDataBase(getConnection(), login)) {
            displayLabelWithGivenText(loginLabel, "Account with that login already exists");
            makeFieldsBorderRed(tfLogin, loginLabel);
            return false;
        }
        basicTheme(tfLogin, loginLabel);
        loginLabel.setVisible(false);
        return true;
    }

    private void checkPasswordRequirementAndSetProperImage(ImageView name, String regexPattern) {
        if (Pattern.matches(regexPattern, tfPassword.getText())) {
            name.setImage(setImage("PasswordIcons", "check"));
        } else {
            name.setImage(setImage("PasswordIcons", "no_check"));
        }
    }

    private void setAllPasswordRequirementImages(boolean visible) {
        Image check = setImage("PasswordIcons", "check");
        Image noCheck = setImage("PasswordIcons", "no_check");
        if (visible) {
            passLowercaseLetterImage.setImage(check);
            passUppercaseLetterImage.setImage(check);
            passSpecialSignImage.setImage(check);
            passLengthImage.setImage(check);
            passNumberImage.setImage(check);
        } else {
            passLowercaseLetterImage.setImage(noCheck);
            passUppercaseLetterImage.setImage(noCheck);
            passSpecialSignImage.setImage(noCheck);
            passLengthImage.setImage(noCheck);
            passNumberImage.setImage(noCheck);
        }
    }

    private boolean checkPasswordComplexity() {
        if (checkPasswordRegex(tfPassword.getText())) {
            setAllPasswordRequirementImages(true);
            return true;
        } else {
            displayLabelWithGivenText(passwordLabel, "Password is not strong enough!");
            makeFieldsBorderRed(tfPassword, passwordLabel);
        }
        return false;
    }


}
