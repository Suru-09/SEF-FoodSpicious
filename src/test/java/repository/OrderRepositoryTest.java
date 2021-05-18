package repository;

import config.DatabaseCredentials;
import domain.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderRepositoryTest extends DatabaseCredentials {

    private Order order;
    private OrderRepository orderRepository;
    private List<Order> orderList;

    public OrderRepositoryTest() {
    }

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository(super.url, super.username, super.password);
        productRepository.getAll();
        UserRepository userRepository = new UserRepository(super.url, super.username, super.password);
        orderRepository = new OrderRepository(super.url, super.username, super.password, productRepository);
        orderList = orderRepository.getAll();
    }

    @AfterEach
    void tearDown() {
        orderRepository = null;
    }

    @Test
    void allOrdersExist() {
        try {
            for(Order o: orderList) {
                order = o;
                assertTrue(orderRepository.orderExists(order.getId()));
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
