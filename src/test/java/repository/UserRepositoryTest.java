package repository;

import config.DatabaseCredentials;
import domain.Admin;
import domain.Customer;

import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryTest extends DatabaseCredentials {

    private final Admin admin  = new Admin("admin",
            "admin",
            "admin",
            "admin",
            "admin",
            "0712345678");

    private Customer customer = new Customer("John",
            "Wick",
            "Ion",
            "DeLaVrancea",
            "Resita",
            "0712345678");

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(super.url, super.username, super.password);
        List<User> list = userRepository.getAll();
        customer.setId((long) (list.size()));
        System.out.println((list.size()));
    }

    @AfterEach
    void tearDown() {
        userRepository = null;
    }

    @Test
    void userExists() {
        try {
            assertTrue(userRepository.userExists(admin.getUsername(), admin.getPassword()));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void addUser() {
        try {
            assertTrue(userRepository.addUser(customer));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteUser()  {
        try {
            User customer1 = userRepository.getUserAfterUsername("John");
            customer1.setPassword("Wick");
            assertTrue(userRepository.deleteUser(customer1));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


}
