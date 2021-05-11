package controller.register;

import com.jfoenix.controls.*;
import config.DatabaseCredentials;
import controller.SceneManager;
import domain.Admin;
import domain.Customer;
import domain.exception.CustomException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import repository.UserRepository;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

//TODO: Comment what the methods do, when you want

public class RegisterController extends DatabaseCredentials implements Initializable {

    @FXML
    private JFXTextField usernameLabel, firstNameLabel, lastNameLabel, addressLabel, phoneLabel;

    @FXML
    private JFXPasswordField passwordLabel;

    @FXML
    private JFXComboBox<String> roleDropDown;

    @FXML
    private Label usernameError, passwordError, firstNameError, lastNameError, addressError, phoneError;

    @FXML
    private Button registerButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    UserRepository userRepository = new UserRepository(super.url, super.username, super.password);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userRepository.getAll();

        usernameError.setVisible(false);
        passwordError.setVisible(false);
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        addressError.setVisible(false);
        phoneError.setVisible(false);

        roleDropDown.getItems().add("Admin");
        roleDropDown.getItems().add("Customer");

        stackPane.setVisible(false);
    }

    public void backButtonClicked(ActionEvent actionEvent) {
            resetFields();
            resetErrorLabels();
            SceneManager.getInstance().switchScene(SceneManager.States.LOGIN);
    }

    public void registerButtonClicked(ActionEvent actionEvent) throws Exception {

        resetErrorLabels();

        boolean b1 = checkUsername();
        boolean b2 = checkPassword();
        boolean b3 = checkName(firstNameLabel.getText(), firstNameError);
        boolean b4 = checkName(lastNameLabel.getText(), lastNameError);
        boolean b5 = checkAddress();
        boolean b6 = checkPhone();

        if(b1 && b2 && b3 && b4 && b5  && b6 && !roleDropDown.getValue().isEmpty() ) {

            boolean bool = true;

            if(roleDropDown.getValue().equals("Admin")) {
                Admin admin = new Admin(usernameLabel.getText(),
                        passwordLabel.getText(),
                        firstNameLabel.getText(),
                        lastNameLabel.getText(),
                        addressLabel.getText(),
                        phoneLabel.getText()
                );
                bool = userRepository.addUser(admin);
            }
            else {
                Customer customer = new Customer(usernameLabel.getText(),
                        passwordLabel.getText(),
                        firstNameLabel.getText(),
                        lastNameLabel.getText(),
                        addressLabel.getText(),
                        phoneLabel.getText()
                );
                bool = userRepository.addUser(customer);
            }

            if(bool)
                loadJFXDialog("You have registered successfully, go back to login", "Registered");
            else {
                loadJFXDialog("This user already exists in the database, try again", "Error");
            }
        }
    }

    public void loadJFXDialog(String string, String header) {

        BoxBlur blur = new BoxBlur(3, 3, 3);

        JFXDialogLayout content = new JFXDialogLayout();
        stackPane.setVisible(true);

        Text headerText = new Text(header);
        headerText.setId("fancyText");
        Text body = new Text(string);
        body.setId("fancyText");

        content.setHeading(headerText);
        content.setBody(body);

        JFXDialog dialog = new JFXDialog(stackPane,content,
                JFXDialog.DialogTransition.TOP);
        JFXButton button = new JFXButton("Okay");
        button.setId("confirmButton");

        button.setOnAction(e -> {
            dialog.close();
        });

        stackPane.setMouseTransparent(false);
        content.setActions(button);
        dialog.show();

        dialog.setOnDialogClosed(e -> {
            stackPane.setMouseTransparent(true);
            stackPane.setVisible(false);
            anchorPane.setEffect(null);
        });

        anchorPane.setEffect(blur);
    }

    private void resetFields() {
        if(!usernameLabel.getText().isEmpty())
            usernameLabel.clear();

        if(!passwordLabel.getText().isEmpty())
            passwordLabel.clear();

        if(!firstNameLabel.getText().isEmpty())
            firstNameLabel.clear();

        if(!lastNameLabel.getText().isEmpty())
            lastNameLabel.clear();

        if(!addressLabel.getText().isEmpty())
            addressLabel.clear();

        if(!phoneLabel.getText().isEmpty() )
            phoneLabel.clear();

        if(roleDropDown.getValue() != null && !roleDropDown.getValue().isEmpty())
            roleDropDown.getSelectionModel().clearSelection();
    }

    private void resetErrorLabels() {
        usernameError.setVisible(false);
        passwordError.setVisible(false);
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        addressError.setVisible(false);
        phoneError.setVisible(false);
    }

    private boolean checkRestrictedValues(String toCheck, Label label) {
        String restrictedValues = " ()[]{}/>.<,'@;:+=-_?`~| ";
        int length = toCheck.length();

        for(int i = 0 ; i < length; ++i) {
            if(restrictedValues.indexOf(toCheck.charAt(i)) != -1) {
                label.setText("Restricted chars used: " + restrictedValues);
                label.setVisible(true);
                return false;
            }
        }
        return true;
    }

    private boolean checkUsername() {

        String username = usernameLabel.getText();
        int length = username.length();

        if(length < 5 || length > 30) {
            usernameError.setText("Length <5 OR >30");
            usernameError.setVisible(true);
            return false;
        }
        return checkRestrictedValues(username, usernameError);
    }

    private boolean checkPassword() {

        String password = passwordLabel.getText();
        int length = password.length();

        if(length < 5 || length > 30) {
            passwordError.setText("Length <5 OR >30");
            passwordError.setVisible(true);
            return false;
        }
        return checkRestrictedValues(password, passwordError);
    }

    private boolean checkName(String name, Label label) {

        if(name.length() < 2 || name.length() > 30) {
            label.setText("Length <5 OR >30");
            label.setVisible(true);
            return false;
        }
        if(!onlyLetters(name)) {
            label.setText("Digits found in the name");
            label.setVisible(true);
            return false;
        }
        return checkRestrictedValues(name, label);
    }

    private boolean checkAddress() {
        String address = addressLabel.getText();
        if(address.length() < 5 || address.length() > 49) {
            addressError.setText("Length <5 OR >50");
            addressError.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean checkPhone() {
        String phone = phoneLabel.getText();
        if(phone.length() != 10) {
            phoneError.setText("Length must be exactly 10");
            phoneError.setVisible(true);
            return false;
        }
        if(!onlyDigits(phone)) {
            phoneError.setText("A phone number has only digits!");
            phoneError.setVisible(true);
            return false;
        }
        return checkRestrictedValues(phone, phoneError);
    }

    private boolean onlyDigits(String string) {
        boolean result = true;
        for (int i = 0; i < string.length(); i++)
            if (!Character.isDigit(string.charAt(i)))
                result = false;
        return result;
    }

    private boolean onlyLetters(String string) {
        for (int i = 0; i < string.length(); i++)
            if (Character.isDigit(string.charAt(i)))
                return false;
        return true;
    }

}


