package gui.Login;


import config.LoginDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    private void reset()
    {
        password.clear();
        username.clear();
    }

    public void loginButtonClicked(ActionEvent actionEvent) {

        String user = username.getText();
        String pw = password.getText();

//        if(user.equals("admin") && pw.equals("admin")) {
//            System.out.println("succesful login");
//        }
//        else {
//            System.out.println("failed");
//        }

        boolean check = LoginDB.checkLoginData(user, pw);

        if (!check) {
            Alert a = new Alert(Alert.AlertType.ERROR, "User or password incorrect");
            a.showAndWait();
        }
        if (check){
            System.out.println("hehehehe");
        }

    }

}

