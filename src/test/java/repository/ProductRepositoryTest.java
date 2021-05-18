package repository;

import config.DatabaseCredentials;
import domain.Product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductRepositoryTest extends DatabaseCredentials {

    private Product prod = new Product(
            "Shaorma",
            "Lipie, carne, cartofi",
            12,
            "29/09/2024");

    private Product test = new Product(
            "test",
            "test",
            15,
            "test"
    );

    private ProductRepository productRepository;
    List<Product> list;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository(super.url, super.username, super.password);
        list = productRepository.getAll();
        System.out.println(list.size());
    }

    @AfterEach
    void tearDown() {
        productRepository = null;
        list = null;
    }

    @Test
    void productExists() {
        try {
            assertTrue(productRepository.productExists(prod.getName()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void addProductDeleteProduct() {
        try {
            assertTrue(productRepository.addProduct(test));

            list = productRepository.getAll();
            Product del = ProductRepository.getProduct((long)list.size());
            assertTrue(productRepository.deleteProduct(del));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
