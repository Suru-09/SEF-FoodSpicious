package controller.profile;

import com.jfoenix.controls.*;
import config.DatabaseCredentials;
import controller.SceneManager;
import domain.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import repository.OrderRepository;
import repository.ProductRepository;
import repository.UserRepository;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerProfileController extends DatabaseCredentials implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane anchorPane;

    @FXML
    private AnchorPane profilePane, orderPane;
    @FXML
    private JFXButton backButton, saveButton, addButton, updateButton, deleteButton;

    @FXML
    private JFXTextField usernameText, firstNameText, lastNameText, addressText, phoneText, orderIdTextField;

    @FXML
    private Button acceptOrderButton, rejectOrderButton;

    @FXML
    private JFXPasswordField passwordText;

    @FXML
    private Label usernameError, passwordError, firstNameError, lastNameError, addressError, phoneError;

    @FXML
    private JFXTextField modifyNameText, modifyIngredientsText, modifyExpirationDateText, modifyPriceText;

    private final UserRepository userRepository = new UserRepository(super.url, super.username, super.password);
    User customerLogged;
    String hashSalt;

    private final ProductRepository productRepository = new ProductRepository(super.url, super.username, super.password);
    List<Product> listOfProducts = productRepository.getAll();

    private final OrderRepository orderRepository = new OrderRepository(super.url, super.username, super.password);
    List<Order> listOfOrders = orderRepository.getAll();

    public void setUsernameText(String usernameText) {
        this.usernameText.setText(usernameText);
    }

    public void setPasswordText(String passwordText) {
        this.passwordText.setText(passwordText);
    }

    public void setFirstNameText(String firstNameText) {
        this.firstNameText.setText(firstNameText);
    }

    public void setLastNameText(String lastNameText) {
        this.lastNameText.setText(lastNameText);
    }

    public void setAddressText(String addressText) {
        this.addressText.setText(addressText);
    }

    public void setPhoneText(String phoneText) {
        this.phoneText.setText(phoneText);
    }

    public void setUserId(long id) {
        customerLogged.setId(id);
    }

    public void setHashSalt(String hashSalt) {
        this.hashSalt = hashSalt;
    }

    public void disableTextFields() {
        usernameText.setDisable(true);
        firstNameText.setDisable(true);
        lastNameText.setDisable(true);
        addressText.setDisable(true);
        phoneText.setDisable(true);
        passwordText.setDisable(true);
    }

    public void setCustomer(User customer) {
        customerLogged = customer;
        customerLogged.setHashSalt(hashSalt);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableTextFields();
        orderPane.setVisible(false);
        profilePane.setVisible(true);
    }

    public void backButtonClicked(ActionEvent actionEvent) {
        orderPane.setVisible(false);
        profilePane.setVisible(true);
        SceneManager.getInstance().switchScene(SceneManager.States.LOGIN);
    }

    public void updateProfileButtonClicked(ActionEvent actionEvent) {
        orderPane.setVisible(false);
        profilePane.setVisible(true);

        saveButton.setVisible(true);

        usernameText.setDisable(false);
        firstNameText.setDisable(false);
        lastNameText.setDisable(false);
        addressText.setDisable(false);
        phoneText.setDisable(false);
        passwordText.setDisable(false);
    }

    public void saveButtonClicked(ActionEvent actionEvent) throws Exception {

        boolean b1 = checkUsername();
        boolean b2 = checkPassword();
        boolean b3 = checkName(firstNameText.getText(), firstNameError);
        boolean b4 = checkName(lastNameText.getText(), lastNameError);
        boolean b5 = checkAddress();
        boolean b6 = checkPhone();

        if(b1 && b2 && b3 && b4 && b5 && b6) {
            Customer updatedCustomer = new Customer(
                    usernameText.getText(),
                    passwordText.getText(),
                    firstNameText.getText(),
                    lastNameText.getText(),
                    addressText.getText(),
                    phoneText.getText()
            );
            updatedCustomer.setId(customerLogged.getId());

            if ( userRepository.updateUser(customerLogged, updatedCustomer) ) {
                disableErrors();
                loadJFXDialog("You have sucessfuly updated your profile", "Succes");
                customerLogged = updatedCustomer;
            }
            else
                System.out.println("FAIL");
        }

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

        String username = usernameText.getText();
        int length = username.length();

        if(length < 5 || length > 30) {
            usernameError.setText("Length <5 OR >30");
            usernameError.setVisible(true);
            return false;
        }
        return checkRestrictedValues(username, usernameError);
    }

    private boolean checkPassword() {

        String password = passwordText.getText();
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
        String address = addressText.getText();
        if(address.length() < 5 || address.length() > 49) {
            addressError.setText("Length <5 OR >50");
            addressError.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean checkPhone() {
        String phone = phoneText.getText();
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

    private void disableErrors() {
        usernameError.setVisible(false);
        passwordError.setVisible(false);
        firstNameError.setVisible(false);
        lastNameError.setVisible(false);
        addressError.setVisible(false);
        phoneError.setVisible(false);
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
}
