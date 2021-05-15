package controller.profile;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import config.DatabaseCredentials;
import controller.SceneManager;
import domain.*;
import domain.exception.CustomException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
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
    private JFXButton backButton, saveButton, addOrderButton;

    @FXML
    private JFXTreeTableView<JFXProduct> orderTable;

    @FXML
    private JFXTextField usernameText, firstNameText, lastNameText, addressText, phoneText, orderIdTextField;

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

    private final OrderRepository orderRepository = new OrderRepository(super.url, super.username, super.password, productRepository);
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
        initializeProductTable();
        setObservableListForProductTable();

        disableTextFields();
        orderPane.setVisible(false);
        profilePane.setVisible(true);
    }


    public void addOrderButtonClicked(ActionEvent actionEvent) throws CustomException {

        Order o = new Order((Customer)customerLogged, Order.Status.PENDING);
        ObservableList<TreeItem<JFXProduct>> prod = orderTable.getSelectionModel().getSelectedItems();
        for(TreeItem<JFXProduct> p : prod) {
            JFXProduct chestie = p.getValue();
            Product product = new Product(chestie.getName(),
                    chestie.getIngredients(),
                    Double.parseDouble(chestie.getPrice()),
                    chestie.getExpirationDate());
            o.addProduct(product);
        }

        System.out.println(o);

        if(o.getProductList().isEmpty()) {
            loadJFXDialog("Your order has no products!", "Error");
        }
        else {
            if( orderRepository.addOrder(o) ) {
                setObservableListForProductTable();

                FXMLLoader loader = SceneManager.getInstance().getFXML(SceneManager.States.ADMIN_LOGGED);
                AdminProfileController controller = loader.getController();
                controller.setOrderRepository(orderRepository);
                controller.setObservableListForOrderTable();
                controller.getOrderTable().refresh();

                loadJFXDialog("Your order has been confirmed!", "Success");
            }
            else {
                loadJFXDialog("There was an error while adding the order!", "Error");
            }
        }
    }

    public void setObservableListForProductTable() {
        ObservableList<JFXProduct> products = FXCollections.observableArrayList();
        for(Product p: productRepository.getAll()) {
            JFXProduct prod = new JFXProduct(
                    p.getName(),
                    p.getIngredients(),
                    p.getExpirationDate(),
                    Double.toString(p.getPrice())
            );
            products.add(prod);
//            System.out.println("DATABASE: " + p);
//            System.out.println("PRODUCT: " + prod);
        }

        final TreeItem<JFXProduct> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);
        orderTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        orderTable.setRoot(root);
        orderTable.setShowRoot(false);
    }

    public void initializeProductTable() {
        JFXTreeTableColumn<JFXProduct, String> productName = new JFXTreeTableColumn<>("Name");
        productName.setPrefWidth(150);
        productName.setResizable(false);
        productName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXProduct, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXProduct, String> param) {
                return param.getValue().getValue().name;
            }
        });

        JFXTreeTableColumn<JFXProduct, String> productIngredients = new JFXTreeTableColumn<>("Ingredients");
        productIngredients.setPrefWidth(150);
        productIngredients.setResizable(false);
        productIngredients.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXProduct, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXProduct, String> param) {
                return param.getValue().getValue().ingredients;
            }
        });

        JFXTreeTableColumn<JFXProduct, String> productExpirationDate = new JFXTreeTableColumn<>("Expiration Date");
        productExpirationDate.setPrefWidth(150);
        productExpirationDate.setResizable(false);
        productExpirationDate.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXProduct, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXProduct, String> param) {
                return param.getValue().getValue().expirationDate;
            }
        });

        JFXTreeTableColumn<JFXProduct, String> productPrice = new JFXTreeTableColumn<>("Price");
        productPrice.setPrefWidth(140);
        productPrice.setResizable(false);

        productPrice.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXProduct, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXProduct, String> param) {
                return param.getValue().getValue().price;
            }
        });

        orderTable.getColumns().setAll(productName, productIngredients, productExpirationDate, productPrice);

    }

    public void processOrderButtonClicked() {
        profilePane.setVisible(false);
        orderPane.setVisible(true);
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
