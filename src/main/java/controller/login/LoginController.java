package controller.login;


import com.jfoenix.controls.*;
import config.DatabaseCredentials;
import controller.SceneManager;
import controller.profile.AdminProfileController;
import controller.profile.CustomerProfileController;
import domain.Admin;
import domain.Customer;
import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import repository.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController extends DatabaseCredentials implements Initializable {

    @FXML
    private JFXTextField username;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXPasswordField password;

    @FXML
    private Button loginButton;

    private final UserRepository userRepository = new UserRepository(super.url, super.username, super.password);

    private void reset() {
        password.clear();
        username.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        stackPane.setVisible(false);
    }

    public void loginButtonClicked() throws Exception {

        String user = username.getText();
        String pw = password.getText();

        boolean check = userRepository.userExists(user, pw);

        if (!check) {
            loadJFXDialog("Username or password incorrect", "Error");
        }

        if (check){
            User userLogged = userRepository.getUser(user ,pw);

            //System.out.println("eu sunt: " + userLogged);

            if(userLogged instanceof Customer) {
                String user_name = userLogged.getUsername();
                //String user_password = userLogged.getPassword();
                String user_firstName = userLogged.getFirstName();
                String user_lastName = userLogged.getLastName();
                String user_address = userLogged.getAddress();
                String user_phone = userLogged.getPhoneNumber();
                String hashSalt = userLogged.getHashSalt();
                long id = userLogged.getId();

                FXMLLoader loader = SceneManager.getInstance().getFXML(SceneManager.States.CUSTOMER_LOGGED);
                CustomerProfileController controller = loader.getController();

                controller.setHashSalt(hashSalt);
                controller.setCustomer(userLogged);
                controller.setUsernameText(user_name);
                controller.setPasswordText("----------");
                controller.setFirstNameText(user_firstName);
                controller.setLastNameText(user_lastName);
                controller.setAddressText(user_address);
                controller.setPhoneText(user_phone);
                controller.setUserId(id);

                SceneManager.getInstance().switchScene(SceneManager.States.CUSTOMER_LOGGED);
            }

            if(userLogged instanceof Admin) {
                String user_name = userLogged.getUsername();
                //String user_password = userLogged.getPassword();
                String user_firstName = userLogged.getFirstName();
                String user_lastName = userLogged.getLastName();
                String user_address = userLogged.getAddress();
                String user_phone = userLogged.getPhoneNumber();
                String hashSalt = userLogged.getHashSalt();
                long id = userLogged.getId();

                FXMLLoader loader = SceneManager.getInstance().getFXML(SceneManager.States.ADMIN_LOGGED);
                AdminProfileController controller = loader.getController();

                controller.setHashSalt(hashSalt);
                controller.setAdmin(userLogged);
                controller.setUsernameText(user_name);
                controller.setPasswordText("----------");
                controller.setFirstNameText(user_firstName);
                controller.setLastNameText(user_lastName);
                controller.setAddressText(user_address);
                controller.setPhoneText(user_phone);
                controller.setUserId(id);

                SceneManager.getInstance().switchScene(SceneManager.States.ADMIN_LOGGED);
            }
        }

    }

    public void registerButtonClicked(ActionEvent actionEvent) throws IOException, SQLException {
        SceneManager.getInstance().switchScene(SceneManager.States.REGISTER);
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

    @FXML
    private void enterPressed(KeyEvent keyEvent) throws Exception {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            loginButtonClicked();
        }
    }
}

