package controller.profile;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import config.DatabaseCredentials;
import controller.SceneManager;
import domain.Admin;
import domain.Order;
import domain.Product;
import domain.User;
import domain.exception.CustomException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import repository.OrderRepository;
import repository.ProductRepository;
import repository.UserRepository;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminProfileController extends DatabaseCredentials implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane anchorPane;

    @FXML
    private AnchorPane profilePane, productPane, orderPane;
    @FXML
    private JFXButton backButton, saveButton, addButton, updateButton, deleteButton;

    @FXML
    private JFXTreeTableView<JFXProduct> productTable;

    @FXML
    private JFXTreeTableView<JFXOrder> orderTable;

    @FXML
    private JFXTextField usernameText, firstNameText, lastNameText, addressText, phoneText, orderIdTextField;

    @FXML
    private Button acceptOrderButton, rejectOrderButton;

    @FXML
    private JFXPasswordField passwordText;

    @FXML
    private Label usernameError, passwordError, firstNameError, lastNameError, addressError, phoneError;

    @FXML
    private VBox crudVBox;

    @FXML
    private Label nameProductListLabel, ingredientsProductListLabel, expirationDateProductListLabel, priceProductListLabel;

    @FXML
    private JFXTextField modifyNameText, modifyIngredientsText, modifyExpirationDateText, modifyPriceText;

    private final UserRepository userRepository = new UserRepository(super.url, super.username, super.password);
    User adminLogged;
    String hashSalt;

    private final ProductRepository productRepository = new ProductRepository(super.url, super.username, super.password);
    List<Product> listOfProducts = productRepository.getAll();

    private OrderRepository orderRepository = new OrderRepository(super.url, super.username, super.password, productRepository);
    List<Order> listOfOrders = orderRepository.getAll();

    public void setOrderRepository(OrderRepository repo) {
        orderRepository = repo;
    }

    public JFXTreeTableView<JFXOrder> getOrderTable() {
        return orderTable;
    }

    public AdminProfileController() {
    }

    public Product getProductToModify() throws SQLException {
        if (!modifyNameText.getText().isEmpty() && !modifyIngredientsText.getText().isEmpty()
                && !modifyExpirationDateText.getText().isEmpty() && !modifyPriceText.getText().isEmpty()) {

            String name = modifyNameText.getText();
            String ingredients = modifyIngredientsText.getText();
            String expirationDate = modifyExpirationDateText.getText();
            double price = Double.parseDouble(modifyPriceText.getText());

            Product prod = new Product(name,
                    ingredients,
                    price,
                    expirationDate);
            long id = productRepository.findProductId(prod);
            prod.setId(id);
            return prod;
        }

        return null;
    }

    public void acceptOrderButtonClicked(ActionEvent actionEvent) {

        if(!orderIdTextField.getText().isEmpty()) {
            Long id = Long.parseUnsignedLong(orderIdTextField.getText());
            if(orderRepository.updateOrder(id, "ACCEPTED") ) {
                loadJFXDialog("The Order has successfully been processed", "Success");
            }
            else {
                loadJFXDialog("The Order hasn't been been processed", "Fail");
            }
        }
        else {
            loadJFXDialog("The Order hasn't been been processed", "Fail");
        }

        setObservableListForOrderTable();
        orderTable.refresh();
    }

    public void rejectOrderButtonClicked(ActionEvent actionEvent) {

        if(!orderIdTextField.getText().isEmpty()) {
            Long id = Long.parseUnsignedLong(orderIdTextField.getText());
            if(orderRepository.updateOrder(id, "REJECTED") ) {
                loadJFXDialog("The Order has successfully been processed", "Success");
            }
            else {
                loadJFXDialog("The Order hasn't been been processed", "Fail");
            }
        }
        else {
            loadJFXDialog("The Order hasn't been been processed", "Fail");
        }

        setObservableListForOrderTable();
        orderTable.refresh();
    }


    public void addButtonClicked(ActionEvent actionEvent) throws CustomException, SQLException {
        Product prod = getProductToModify();

        if(prod == null) {
            loadJFXDialog("There was an error adding the product", "Error");
            return;
        }

        boolean check = productRepository.addProduct(prod);

        if( !check ) {
            loadJFXDialog("There was an error adding the product", "Error");
        }
        else {
            listOfProducts = productRepository.getAll();
            loadJFXDialog("You have added the product successfully", "Success");
            setObservableListForProductTable();
            productTable.refresh();
        }

    }

    public void updateButtonClicked(ActionEvent actionEvent) throws SQLException {
        Product prod = getProductToModify();

        if(prod == null) {
            loadJFXDialog("There was an error updating the product", "Error");
            return;
        }

        boolean check = productRepository.updateProduct(prod);

        if( !check ) {
            loadJFXDialog("There was an error updating the product", "Error");
        }
        else {
            loadJFXDialog("You have updated the product successfully", "Success");
            setObservableListForProductTable();
            productTable.refresh();
        }

    }

    public void deleteButtonClicked(ActionEvent actionEvent) throws CustomException, SQLException {
        Product prod = getProductToModify();

        if(prod == null) {
            loadJFXDialog("There was an error adding the product", "Error");
            return;
        }

        boolean check = productRepository.deleteProduct(prod);

        if( !check ) {
            loadJFXDialog("There was an error deleting the product", "Error");
        }
        else {
            loadJFXDialog("You have deleted the product successfully", "Success");
            setObservableListForProductTable();
            productTable.refresh();
        }
    }

    public void clearModifyTextField() {
        modifyNameText.clear();
        modifyIngredientsText.clear();
        modifyExpirationDateText.clear();
        modifyPriceText.clear();
    }

    public void setObservableListForProductTable() {
        ObservableList<JFXProduct> products = FXCollections.observableArrayList();
        for(Product p: new ArrayList<>(productRepository.getHashMap().values())) {
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
        productTable.setRoot(root);
        productTable.setShowRoot(false);
    }

    public void setObservableListForOrderTable() {
        ObservableList<JFXOrder> orders = FXCollections.observableArrayList();
        for(Order o: new ArrayList<>(orderRepository.getHashMap().values())){
            JFXOrder ord = new JFXOrder(
                    o.getId(),
                    o.getCustomer().getId(),
                    o.getStatus().toString()
            );
            if(o.getStatus() == Order.Status.PENDING)
                orders.add(ord);
        }

        final TreeItem<JFXOrder> root = new RecursiveTreeItem<>(orders, RecursiveTreeObject::getChildren);
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

        productTable.getColumns().setAll(productName, productIngredients, productExpirationDate, productPrice);

    }

    public void initializeOrderTable() {
        JFXTreeTableColumn<JFXOrder, String> orderID = new JFXTreeTableColumn<>("Order ID");
        orderID.setPrefWidth(150);
        orderID.setResizable(false);
        orderID.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXOrder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXOrder, String> param) {
                return param.getValue().getValue().orderID;
            }
        });

        JFXTreeTableColumn<JFXOrder, String> orderStatus = new JFXTreeTableColumn<>("Order Status");
        orderStatus.setPrefWidth(150);
        orderStatus.setResizable(false);
        orderStatus.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXOrder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXOrder, String> param) {
                return param.getValue().getValue().status;
            }
        });

        JFXTreeTableColumn<JFXOrder, String> orderCustomerId = new JFXTreeTableColumn<>("Customer ID");
        orderCustomerId .setPrefWidth(140);
        orderStatus.setResizable(false);
        orderCustomerId .setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<JFXOrder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<JFXOrder, String> param) {
                return param.getValue().getValue().customerId;
            }
        });

        orderTable.getColumns().setAll(orderID, orderCustomerId, orderStatus);
    }

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
        adminLogged.setId(id);
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

    public void setAdmin(User admin) {
        adminLogged = admin;
        adminLogged.setHashSalt(hashSalt);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeOrderTable();
        setObservableListForOrderTable();
        initializeProductTable();
        setObservableListForProductTable();
        disableTextFields();
        orderPane.setVisible(false);
        profilePane.setVisible(true);
        productPane.setVisible(false);
    }


    public void backButtonClicked(ActionEvent actionEvent) {
        orderPane.setVisible(false);
        profilePane.setVisible(true);
        productPane.setVisible(false);
        SceneManager.getInstance().switchScene(SceneManager.States.LOGIN);
    }

    public void updateProfileButtonClicked(ActionEvent actionEvent) {
        productPane.setVisible(false);
        orderPane.setVisible(false);
        profilePane.setVisible(true);

        saveButton.setVisible(true);

        usernameText.setDisable(false);
        firstNameText.setDisable(false);
        lastNameText.setDisable(false);
        addressText.setDisable(false);
        phoneText.setDisable(false);
        passwordText.setDisable(false);
        clearModifyTextField();
    }

    public void productListButtonClicked(ActionEvent actionEvent) {

        disableTextFields();
        productPane.setVisible(true);
        profilePane.setVisible(false);
        orderPane.setVisible(false);
        saveButton.setVisible(false);
    }

    public void processOrderButtonClicked(ActionEvent actionEvent) {
        orderPane.setVisible(true);
        productPane.setVisible(false);
        profilePane.setVisible(false);
        disableTextFields();
        clearModifyTextField();
        saveButton.setVisible(false);
    }

    public void saveButtonClicked(ActionEvent actionEvent) throws Exception {

        boolean b1 = checkUsername();
        boolean b2 = checkPassword();
        boolean b3 = checkName(firstNameText.getText(), firstNameError);
        boolean b4 = checkName(lastNameText.getText(), lastNameError);
        boolean b5 = checkAddress();
        boolean b6 = checkPhone();

        if(b1 && b2 && b3 && b4 && b5 && b6) {
            Admin updatedAdmin = new Admin(
                    usernameText.getText(),
                    passwordText.getText(),
                    firstNameText.getText(),
                    lastNameText.getText(),
                    addressText.getText(),
                    phoneText.getText()
            );
            updatedAdmin.setId(adminLogged.getId());

            if ( userRepository.updateUser(adminLogged, updatedAdmin) ) {
                disableErrors();
                loadJFXDialog("You have sucessfuly updated your profile", "Succes");
                adminLogged = updatedAdmin;
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
