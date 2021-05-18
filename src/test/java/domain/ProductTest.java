package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {

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

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(name,
                ingredients,
                price,
                expirationDate);
        product.setId(ID);
    }

    @AfterEach
    void tearDown() {
        product = null;
    }

    @Test
    void getID() {
        assertEquals(product.getId(), ID);
    }

    @Test
    void setID() {
        product.setId(newID);
        assertEquals(product.getId(),newID);
    }

    @Test
    void getName() {
        assertEquals(product.getName(), name);
    }

    @Test
    void setName() {
        product.setName(newName);
        assertEquals(product.getName(), newName);
    }

    @Test
    void getIngredients() {
        assertEquals(product.getIngredients(), ingredients);
    }

    @Test
    void setIngredients() {
        product.setIngredients(newIngredients);
        assertEquals(product.getIngredients(), newIngredients);
    }

    @Test
    void getPrice() {
        assertEquals(product.getPrice(), price);
    }

    @Test
    void setPrice() {
        product.setPrice(newPrice);
        assertEquals(product.getPrice(), newPrice);
    }

    @Test
    void getExpirationDate() {
        assertEquals(product.getExpirationDate(), expirationDate);
    }

    @Test
    void setExpirationDate() {
        product.setExpirationDate(newExpirationDate);
        assertEquals(product.getExpirationDate(), newExpirationDate);
    }

    @Test
    void equals() {
        Product  anotherProduct = new Product(name,
                ingredients,
                price,
                expirationDate);
        product.setId(ID);
        assertEquals(product, anotherProduct);
    }
}
