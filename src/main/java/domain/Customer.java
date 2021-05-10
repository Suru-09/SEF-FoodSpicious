package domain;

import java.util.Objects;

public class Customer extends User {

    private final boolean isAdmin = false;

    public Customer(String username, String password, String firstName, String lastName, String address, String phoneNumber) {
        super(username, password, firstName, lastName, address, phoneNumber);
    }

    public Customer() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return isAdmin == customer.isAdmin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isAdmin);
    }

    @Override
    public String toString() {
        return super.toString() + "Customer{" +
                "isAdmin=" + isAdmin +
                '}';
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
