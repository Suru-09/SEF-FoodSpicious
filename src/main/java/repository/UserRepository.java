package repository;

import config.HashPassword;
import domain.Admin;
import domain.Customer;
import domain.User;
import domain.exception.CustomException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: The UserRepository class, represents the interface of the application
 * with the database, everything you want to modify in the database related to
 * the users, will be done through this repo
 */

public class UserRepository extends AbstractRepository<Long, User> {

    private static String url;
    private static String username;
    private static String password;


    /**
     * @param url url for the database
     * @param user  user of the database
     * @param password password of the the database
     */

    public UserRepository(String url, String user, String password) {
        UserRepository.url = url;
        username = user;
        UserRepository.password = password;
    }

    /**
     * This method loads all the users from the database in the memory
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     */
    private void loadData() throws SQLException {

        String sql = "select * from \"user\" ";

        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String address = rs.getString("address");
                String phone = rs.getString("phonenumber");
                String hashSalt = rs.getString("hashSalt");
                boolean isAdmin = rs.getBoolean("isadmin");

                if (isAdmin) {

                    Admin admin = new Admin(username,
                            password,
                            firstName,
                            lastName,
                            address,
                            phone);

                    admin.setHashSalt(hashSalt);
                    admin.setId(id);
                    super.add(admin);
                } else {
                    Customer customer = new Customer(username,
                            password,
                            firstName,
                            lastName,
                            address,
                            phone);

                    customer.setHashSalt(hashSalt);
                    customer.setId(id);
                    super.add(customer);
                }

            }
        } catch (CustomException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param user The user that needs to be updated
     * @param userUpdated The updated version of the previous user
     * @return true if the operation was done successfully, false otherwise
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     */
    public boolean updateUser(User user, User userUpdated) throws Exception {

        int id_user = findUserId(user);

        boolean isAdmin = false;

        if ( (userUpdated instanceof Admin) ) {
            isAdmin = ((Admin)userUpdated).isAdmin();
        }

        if ( (userUpdated instanceof Customer) ) {
            isAdmin = ((Customer)userUpdated).isAdmin();
        }

        String salt = user.getHashSalt();
        String encryptedPassword = HashPassword.getEncryptedPassword(userUpdated.getPassword(), salt);

        String sql = "update \"user\"  SET id = " + "'" + userUpdated.getId() + "', "
                + "username = " + createTemplate(userUpdated.getUsername())
                + "password = " + createTemplate(encryptedPassword)
                + "firstname = " + createTemplate(userUpdated.getFirstName())
                + "lastname = " + createTemplate(userUpdated.getLastName())
                + "address = " + createTemplate(userUpdated.getAddress())
                + "phonenumber = " + createTemplate(userUpdated.getPhoneNumber())
                + "isadmin = " +"'" + isAdmin + "'" + "where id = " + "'" + id_user + "';";

        try (var connection = DriverManager.getConnection(url, username, password)

        ) {
            var ps = connection.prepareStatement(sql);
            var rs = ps.executeUpdate();

            return true;
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param user The user that will be added in the database
     * @return  true if the user was added, false otherwise
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     * @throws CustomException If the user already exists in the database,
     * there will be thrown a duplicated value custom exception
     */
    public boolean addUser(User user) throws Exception {


        String sql = "";
        String Username = user.getUsername();
        String Password = user.getPassword();
        String Firstname = user.getFirstName();
        String Lastname = user.getLastName();
        String Address = user.getAddress();
        String Phone = user.getPhoneNumber();

        boolean isAdmin = false;

        //Securing the given password
        String salt = HashPassword.getNewSalt();
        String encryptedPassword = HashPassword.getEncryptedPassword(Password, salt);
        user.setHashSalt(salt);

        System.out.println("SALT: " + salt);
        System.out.println("PASSWORD: " + encryptedPassword);

        //TODO: Implement the duplicated message into the GUI,
        // instead of using an exception

        if(userExists(user.getUsername(), user.getPassword()) ) {
            return false;
        }

        if( (user instanceof Admin) ) {
            isAdmin = ((Admin) user).isAdmin();
        }

        if( (user instanceof Customer) ) {
            isAdmin = ((Customer) user).isAdmin();
        }

        int new_id = super.elems.size() + 1;

        try (var connection = DriverManager.getConnection(url, username, password)

        ) {

            sql = "insert into \"user\" "
                    + "VALUES(" + "'" + new_id + "'"  + ", "
                    + createTemplate(Username)
                    + "'" + encryptedPassword + "', "
                    + createTemplate(Firstname)
                    + createTemplate(Lastname)
                    + createTemplate(Address)
                    + createTemplate(Phone)
                    + "'" + isAdmin + "'" + " ,"
                     + "'" + salt + "'" +
                    ");";

            var ps = connection.prepareStatement(sql);
            var rs = ps.executeUpdate();

            return true;
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param Username A user's username
     * @param Password A user's password
     * @return True if the user exists in the database, false otherwise
     * @throws SQLException If there is the case, there will be thrown
     *a SqlException while using the database
     */
    public boolean userExists(String Username, String Password) throws Exception {

        String sql = "select * from \"user\" ";

        User user = getUserAfterUsername(Username);
        String salt = null;
        if(user != null) {
            salt = user.getHashSalt();
        }else {
            return false;
        }

        String encryptedPassword = HashPassword.getEncryptedPassword(Password, salt);

        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {

                String UserName = rs.getString("username");
                String PassWord = rs.getString("password");

                if( UserName.equals(Username) && PassWord.equals(encryptedPassword) )
                    return true;
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * @param user The user which must be deleted
     * @return true if the user has been deleted, false otherwise
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     * @throws CustomException If the user doesn't exist in the database,
     *there will be thrown a non-existing user custom exception
     */
    public boolean deleteUser(User user) throws Exception {

        //If user doesn't exist, we can't delete it

        int idFromWhereToUpdate = findUserId(user);
        if(idFromWhereToUpdate < 0)
            return false;

        if( userExists(user.getUsername(), user.getPassword()) ) {
            String sql = "delete from \"user\" " +
                         "where username = " + "'" +
                    user.getUsername() + "'" + " AND "
                    + "password = " + "'" + user.getPassword() + "';";

            try (var connection = DriverManager.getConnection(url, username, password)
            ) {
                var ps = connection.prepareStatement(sql);
                var rs = ps.executeUpdate();
                //deleting from the memory repository
                super.elems.remove(user.getId());

                if( updateIndexes(idFromWhereToUpdate) ) {
                    return true;
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            //TODO: something will need to be done here to solve
            // this thing to be shown in GUI
            throw new CustomException("You're trying to delete a non-existent user!");
        }
        return false;
    }

    /**
     * @param user The user for whom we search the ID
     * @return the id(int > 0) that is searched, -1 if the ID
     * doesn't exist
     */
    public int findUserId(User user) {

        String sql = "Select * from \"user\" where " +
                "username =" + "'" + user.getUsername() + "'" +
                "AND password=" + "'" + user.getPassword() + "'" + " ;";

        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()
        ) {

            while( rs.next() ) {
                int id = -1;
                id = rs.getInt("id");
                if(id > 0)
                    return id;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * @param id the id from which we start
     *           updating the indexes
     * @return true if the indexes were updated,
     * false otherwise
     */
    public boolean updateIndexes(int id) {

        String sql = "update \"user\" SET id = id - 1" +
                "where id > " + id;

        try (var connection = DriverManager.getConnection(url, username, password)
        ) {

            var ps = connection.prepareStatement(sql);
            var rs = ps.executeUpdate();
            return true;
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param str The string for which we create the template
     * @return The formated string for the SQL querry
     */
    private static String createTemplate(String str) {
        StringBuilder tmp = new StringBuilder();
        tmp.append("'").append(str).append("'").append(", ");

        return tmp.toString();
    }

    /**
     * @param customerId The customer's id who is being searched
     * @return the customer if it exists, null otherwise
     */
    public static Customer getCustomer(Long customerId) {
        String customerSql = "select * from \"user\" " + "where id=" + customerId;
        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(customerSql);
             var rs = ps.executeQuery()) {

            rs.next();

            Long id = rs.getLong("id");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            String address = rs.getString("address");
            String phoneNumber = rs.getString("phonenumber");
            String hashSalt = rs.getString("hashSalt");
            boolean isAdmin = rs.getBoolean("isadmin");

            if (isAdmin) {
                return null;
            }

            Customer customer = new Customer(username, password, firstname, lastname, address, phoneNumber);
            customer.setHashSalt(hashSalt);
            customer.setId(id);

            return customer;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param user_name The username
     * @param pass_word the password
     * @return the user if found, null otherwise
     * @throws Exception in case the hashing fails somehow
     */
    public  User getUser(String user_name, String pass_word) throws Exception {

        User user = getUserAfterUsername(user_name);
        String salt = "";
        if(user == null)
            return null;
        else {
            salt = user.getHashSalt();
        }

        String encryptedPassword = HashPassword.getEncryptedPassword(pass_word, salt);

        String customerSql = "select * from \"user\" where username=" + "'" + user_name + "'"
                + " AND password=" + "'" + encryptedPassword + "'";
        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(customerSql);
             var rs = ps.executeQuery()) {

            while(rs.next()) {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phonenumber");
                String hashSalt = rs.getString("hashSalt");
                boolean isAdmin = rs.getBoolean("isadmin");

                if (isAdmin) {
                    Admin admin = new Admin(username
                            , password
                            , firstname
                            , lastname
                            , address
                            , phoneNumber);
                    admin.setHashSalt(hashSalt);
                    admin.setId(id);

                    return admin;
                }
                else {
                    Customer customer = new Customer(username
                            , password
                            , firstname
                            , lastname
                            , address
                            , phoneNumber);
                    customer.setHashSalt(hashSalt);
                    customer.setId(id);

                    return customer;
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param user_name The username
     * @return the user if found, null otherwise
     */
    public  User getUserAfterUsername(String user_name) {
        String customerSql = "select * from \"user\" where username=" + "'" + user_name + "'";
        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(customerSql);
             var rs = ps.executeQuery()) {

            while(rs.next()) {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phonenumber");
                String hashSalt = rs.getString("hashSalt");
                boolean isAdmin = rs.getBoolean("isadmin");

                if (isAdmin) {
                    Admin admin = new Admin(username
                            , password
                            , firstname
                            , lastname
                            , address
                            , phoneNumber);
                    admin.setHashSalt(hashSalt);
                    admin.setId(id);

                    return admin;
                }
                else if(!isAdmin){
                    Customer customer = new Customer(username
                            , password
                            , firstname
                            , lastname
                            , address
                            , phoneNumber);
                    customer.setHashSalt(hashSalt);
                    customer.setId(id);

                    return customer;
                }
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Returns a List of all the users in the
     * repository
     */
    public List<User> getAll() {
        try {
            loadData();
            return new ArrayList<>(super.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(super.getAll());
    }
}