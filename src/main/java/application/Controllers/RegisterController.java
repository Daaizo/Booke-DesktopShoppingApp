package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import users.Client;

import java.sql.SQLException;
import java.util.Objects;
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
    String password;
    @FXML
    private Button showPasswordButton, showRepeatPasswordButton;

    @FXML
    public void initialize() {
        AnchorPane mainAnchor = setAnchorSizeAndColors();
        mainAnchor.getStylesheets().add(Objects.requireNonNull(cssUrl).toExternalForm());
        mainAnchor.getChildren().addAll(createHorizontalLine(), setSmallLogoInCorner());
        createExitButton();
        createGoBackButton(event -> switchScene(event, loginScene));
        setAllPasswordRequirementImages(false);
        setPasswordVisibilityButtons();
        setPasswordFieldListener();
        seTextFieldListeners();
    }

    private void setPasswordFieldListener() {
        tfPassword.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                if (Pattern.matches(PASSWORDS_REGEX, newValue)) {
                    setAllPasswordRequirementImages(true);
                } else {
                    checkPasswordRequirementAndSetProperImage(passUppercaseLetterImage, "(.*[A-Z].*)");  // if string contains at least one : // uppercase latter
                    checkPasswordRequirementAndSetProperImage(passSpecialSignImage, "(.*[!@#$&%^&*()_+].*)"); // special sign
                    checkPasswordRequirementAndSetProperImage(passLengthImage, "^.{6,20}$"); // 6-20 characters
                    checkPasswordRequirementAndSetProperImage(passNumberImage, "(.*[0-9].*)"); // number
                    checkPasswordRequirementAndSetProperImage(passLowercaseLetterImage, "(.*[a-z].*)"); //lowercase
                }
            }
            basicTheme(tfPassword, passwordLabel);
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);
        });
    }

    private void seTextFieldListeners() {
        tfLogin.textProperty().addListener((observableValue, s, t1) -> basicTheme(tfLogin, loginLabel));
        tfName.textProperty().addListener((observableValue, s, t1) -> basicTheme(tfName, nameLabel));
        tfLastName.textProperty().addListener((observableValue, s, t1) -> basicTheme(tfLastName, lastnameLabel));
        tfPasswordRepeat.textProperty().addListener((observableValue, s, t1) -> {
            basicTheme(tfPassword, passwordLabel);
            basicTheme(tfPasswordRepeat, repeatPasswordLabel);
        });
    }

    protected void setPasswordVisibilityButtons() {
        String hiddenPassIconName = "hiddenPassword.png";
        String showPassIconName = "showPassword.png";
        showPasswordButton.setGraphic(setImageFromIconsFolder("hiddenPassword.png"));
        showRepeatPasswordButton.setGraphic(setImageFromIconsFolder("hiddenPassword.png"));
        showPasswordButton.setBackground(Background.EMPTY);
        showRepeatPasswordButton.setBackground(Background.EMPTY);
        showPasswordButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> showPasswordButtonPressed(tfPassword, showPasswordButton, setImageFromIconsFolder(showPassIconName)));
        showPasswordButton.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> showPasswordButtonReleased(tfPassword, showPasswordButton, setImageFromIconsFolder(hiddenPassIconName)));
        showRepeatPasswordButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> showPasswordButtonPressed(tfPasswordRepeat, showRepeatPasswordButton, setImageFromIconsFolder(showPassIconName)));
        showRepeatPasswordButton.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> showPasswordButtonReleased(tfPasswordRepeat, showRepeatPasswordButton, setImageFromIconsFolder(hiddenPassIconName)));
    }

    private void showPasswordButtonPressed(TextField field, Button button, ImageView graphic) {

        button.setGraphic(graphic);
        password = field.getText();
        field.clear();
        field.setPromptText(password);
    }

    private void showPasswordButtonReleased(TextField field, Button button, ImageView graphic) {
        button.setGraphic(graphic);
        field.setText(password);
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

    private boolean areFieldsFilledCorrectlyAndLoginIsUnique() throws SQLException {
        return !isLoginEmpty() && !isNameEmpty() && !isLastNameEmpty() && !isPasswordEmpty() && checkPasswordComplexity() && passFieldMatches() && isLoginUnique() && isCheckboxChecked();
    }

    private Client newUser() {
        String login = tfLogin.getText().trim();
        String name = tfName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String password = tfPassword.getText().trim();
        return new Client(login, name, lastName, password);
    }


    @FXML
    void registerButtonClicked(ActionEvent event) {

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

    private boolean isLoginUnique() throws SQLException {
        checkConnectionWithDb();
        String login = tfLogin.getText().trim();
        if (Client.isClientInDataBase(getConnection(), login)) {
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
            name.setImage(new Image(iconsUrl + "check.png"));
        } else {
            name.setImage(new Image(iconsUrl + "no_check.png"));
        }
    }

    private void setAllPasswordRequirementImages(boolean visible) {
        Image check = new Image(iconsUrl + "check.png");
        Image noCheck = new Image(iconsUrl + "no_check.png");
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

        if (Pattern.matches(PASSWORDS_REGEX, tfPassword.getText())) {
            setAllPasswordRequirementImages(true);
            colorField(tfPassword, passwordLabel, Color.GREEN);
            colorField(tfPasswordRepeat, repeatPasswordLabel, Color.GREEN);
            return true;
        } else {
            displayLabelWithGivenText(passwordLabel, "Password is not strong enough!");
            colorField(tfPassword, passwordLabel, Color.RED);
            colorField(tfPasswordRepeat, repeatPasswordLabel, Color.RED);
            checkPasswordRequirementAndSetProperImage(passUppercaseLetterImage, "(.*[A-Z].*)");  // if string contains at least one : // uppercase latter
            checkPasswordRequirementAndSetProperImage(passSpecialSignImage, "(.*[!@#$&%^&*()_+].*)"); // special sign
            checkPasswordRequirementAndSetProperImage(passLengthImage, "^.{6,20}$"); // 6-20 characters
            checkPasswordRequirementAndSetProperImage(passNumberImage, "(.*[0-9].*)"); // number
            checkPasswordRequirementAndSetProperImage(passLowercaseLetterImage, "(.*[a-z].*)"); //lowercase
        }
        return false;
    }


}
