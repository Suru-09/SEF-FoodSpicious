package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import domain.Order.Status;
import domain.*;

import java.util.ArrayList;
import java.util.List;

public class OrderTest {

    private final Status status = Status.PENDING;
    private final Status newStatus = Status.ACCEPTED;

    private final String name = "Shaorma";
    private final String newName = "Burger";
    private final String ingredients = "Lipie si carne";
    private final String newIngredients = "Chifle si carne de vita";
    private final double price = 13;
    private final double newPrice = 15;
    private final String expirationDate = "19/10/2022";
    private final String newExpirationDate = "21/03/2023";
    private final Long ID = 4L;
    private final Long newID = 1L;

    Customer customer1 = new Customer("ionica",
            "AmMereFaine",
            "Ion",
            "Viteazu",
            "Bucuresti",
            "0712345678");
    Customer customer2 = new Customer("Gard",
            "parola",
            "John",
            "Snow",
            "France",
            "0795743271");

    Product product1 =  new Product(name,
            ingredients,
            price,
            expirationDate);
    Product product2 = new Product(newName,
            newIngredients,
            newPrice,
            newExpirationDate);

    private List<Product> prodList = new ArrayList<>();
    private List<Product> newProdList = new ArrayList<>();

    private Order order;

    @BeforeEach
    void setUp() {
        product1.setId(ID);
        product2.setId(newID);
        prodList.add(product1);
        prodList.add(product2);
        newProdList.add(product1);
        newProdList.add(product2);
        newProdList.add(product1);

        customer1.setId(ID);
        customer2.setId(ID);

        order = new Order(
                customer1,
                status
        );
        order.setId(ID);
        order.addProduct(product1);
        order.addProduct(product2);
    }

    @AfterEach
    void tearDown() {
        order = null;
    }

    @Test
    void getProductList() {
        assertEquals(order.getProductList(), prodList);
    }

    @Test
    void setProductList() {
        order.setProduct(newProdList);
        assertEquals(order.getProductList(), newProdList);
    }

    @Test
    void getCustomer() {
        assertEquals(order.getCustomer(), customer1);
    }

    @Test
    void setCustomer() {
        order.setCustomer(customer2);
        assertEquals(order.getCustomer(), customer2);
    }

    @Test
    void getStatus() {
        assertEquals(order.getStatus(), status);
    }

    @Test
    void setStatus() {
        order.setStatus(newStatus);
        assertEquals(order.getStatus(), newStatus);

    }
}
