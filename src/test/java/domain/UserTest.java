package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    private static final String username = "Dogs";
    private static final String newUsername = "Kittens";
    private static final String password = "Oranges";
    private static final String newPassword = "Apples";
    private static final String firstName = "John";
    private static final String newFirstName = "Ion";
    private static final String lastName = "Wick";
    private static final String newLastName = "Wicked";
    private static final String address = "New York";
    private static final String newAddress = "Los Angeles";
    private static final String phoneNumber = "0734562378";
    private static final String newPhoneNumber = "0712345678";
    private static final String hashSalt = "ABCDE09231";
    private static final String newHashSalt = "ZXTV12MP";
    private static final Long ID = 2L;
    private static final Long newID = 3L;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(username, password,
                        firstName, lastName,
                        address, phoneNumber);
        user.setId(ID);
        user.setHashSalt(hashSalt);
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    void getId() {
        assertEquals(user.getId(), ID);
    }

    @Test
    void setId() {
        user.setId(newID);
        assertEquals(user.getId(), newID);
    }

    @Test
    void getUsername() {
        assertEquals(user.getUsername(), username);
    }

    @Test
    void setUsername() {
        user.setUsername(newUsername);
        assertEquals(user.getUsername(), newUsername);
    }

    @Test
    void getPassword() {
        assertEquals(user.getPassword(), password);
    }

    @Test
    void setPassword() {
        user.setPassword(newPassword);
        assertEquals(user.getPassword(), newPassword);
    }

    @Test
    void getFirstName() {
        assertEquals(user.getFirstName(), firstName);
    }

    @Test
    void setFirstName() {
        user.setFirstName(newFirstName);
        assertEquals(user.getFirstName(), newFirstName);
    }

    @Test
    void getLastName() {
        assertEquals(user.getLastName(), lastName);
    }

    @Test
    void setLastName() {
        user.setLastName(newLastName);
        assertEquals(user.getLastName(), newLastName);
    }

    @Test
    void getAddress() {
        assertEquals(user.getAddress(), address);
    }

    @Test
    void setAddress() {
        user.setAddress(newAddress);
        assertEquals(user.getAddress(), newAddress);
    }

    @Test
    void getPhoneNumber() {
        assertEquals(user.getPhoneNumber(), phoneNumber);
    }

    @Test
    void setPhoneNumber() {
        user.setPhoneNumber(newPhoneNumber);
        assertEquals(user.getPhoneNumber(), newPhoneNumber);
    }

    @Test
    void getHashSalt() {
        assertEquals(user.getHashSalt(), hashSalt);
    }

    @Test
    void setHashSalt() {
        user.setHashSalt(newHashSalt);
        assertEquals(user.getHashSalt(), newHashSalt);
    }

    @Test
    void equals() {
        User anotherUser = new User(username, password,
                firstName, lastName,
                address, phoneNumber);
        anotherUser.setId(ID);
        anotherUser.setHashSalt(hashSalt);
        assertEquals(user , anotherUser);
    }
}
