package domain;

public class Admin extends User {

    private final boolean isAdmin = true;

    public Admin(String username, String password, String firstName, String lastName, String address, String phoneNumber) {
        super(username, password, firstName, lastName, address, phoneNumber);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return super.toString() + "Admin{" +
                "isAdmin=" + isAdmin +
                '}';
    }
}
