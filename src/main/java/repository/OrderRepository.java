package repository;

import domain.Customer;
import domain.Order;
import domain.Product;
import domain.exception.CustomException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static repository.ProductRepository.getProduct;
import static repository.UserRepository.getCustomer;

//TODO: Finish the update function

public class OrderRepository extends AbstractRepository<Long, Order> {
    private final String url;
    private final String user;
    private final String password;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Database Credentials
     * @param url url of the database
     * @param user ursername of the database
     * @param password password for the database
     */
    public OrderRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        productRepository = new ProductRepository(url, user, password);
        userRepository = new UserRepository(url, user, password);
        productRepository.getAll();
        userRepository.getAll();
    }

    /**
     * This function loads data from the database into the memory
     * @throws SQLException If there is the case, there will be thrown
     *a SqlException while using the database
     */
    private void loadData() throws SQLException {
        super.elems.clear();
        String sql = "select * from \"order\"";

        try (var connection = DriverManager.getConnection(url, user, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                Long customerId = rs.getLong("customer");
                String status = rs.getString("status");

                String sql2 = "Select * from \"order_product\" " +
                        "where order_id = " + id + ";";

                try (var connection2 = DriverManager.getConnection(url, user, password);
                     var ps2 = connection.prepareStatement(sql2);
                     var rs2 = ps2.executeQuery()) {

                    Customer customer = getCustomer(customerId);
                    Order o = new Order(customer, Order.Status.valueOf(status));

                    while (rs2.next()) {
                        long product_id = rs2.getInt("product_id");
                        Product product = getProduct(product_id);

                        if (product == null) {
                            System.err.println("product null");
                            throw new CustomException("The product extracted is null!");
                        } else {

                            if (customer == null) {
                                System.err.println("customer null");
                                throw new CustomException("The customer extracted is null!");

                            } else {
                                o.setId(id);
                                o.addProduct(product);
                            }
                        }
                    }

                    if(o.getProductList().isEmpty()) {
                        System.err.println("Product list empty in order!");
                        throw new CustomException("The product list in the order can't be empty");
                    }else {
                        super.add(o);
                    }

                } catch (CustomException e) {
                    e.printStackTrace();
                }
            }
        } catch(SQLException e){
                e.printStackTrace();
                }
    }

    /**
     * @param o The Order that needs to be added
     * @return true if the order has been added,
     * false otherwise
     * @throws CustomException Already existing custom
     * exception
     */
    public boolean addOrder(Order o) throws CustomException {
        String sql;
        int new_id = super.elems.size() + 1;

        //TODO: do this exception in GUI
        if(orderExists(o.getId() ) ) {
            throw new CustomException("Hey, you're adding an order which already exists");
        }

        try (var connection = DriverManager.getConnection(url, user, password)

        ) {
            sql = "insert into \"order\" "
                    + "VALUES(" + "'" + new_id + "'"  + ", "
                    + "'" + o.getCustomer().getId() + "'" + ", "
                    + "'" + o.getStatus().toString() + "'"
                    + ");";

            var ps = connection.prepareStatement(sql);
            var rs = ps.executeUpdate();

           List<Product> productList = o.getProductList();

            try (var connection2 = DriverManager.getConnection(url, user, password)
            ) {
                String sql2 = "";
                for(Product p: productList) {
                    System.out.println("PRODUCT: " + p);
                    sql2 = "insert into \"order_product\" " +
                            "VALUES(" + "'" + new_id + "'" + ", "
                        + "'" + productRepository.findProductId(p) + "'" + ");";

                    var ps2 = connection2.prepareStatement(sql2);
                    var rs2 = ps2.executeUpdate();
                }

                return true;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * !!! Important, in order to add correctly, data should already
     * have been loaded from the database
     * @param customerId The id of the customer which has the order
     * @param status The status of the order
     * @return true if the order exists, false otherwise
     */
    public boolean orderExists(Long orderID) {

        String sql = "Select * from \"order\" ";

        try(var connection = DriverManager.getConnection(url, user, password);
            var ps = connection.prepareStatement(sql);
            var rs = ps.executeQuery()
        ) {
            while( rs.next() ) {
                long order_id = rs.getInt("id");
//                System.out.println("CUSTOMER_ID: " + customer_id + " customer_id " + customerId);
//                System.out.println("STATUS: " + status);

                if(order_id == orderID)
                    return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param o The order that needs to be deleted
     * @return true if the order has been deleted,
     * false otherwise
     */
    public boolean deleteOrder(Order o)  {
        String sql = "Delete from \"order\" where id = " + o.getId()
                + "AND customer=" + o.getCustomer().getId()
                + "AND status=" + "'" + o.getStatus().toString() + "'" + ";";

        if(!orderExists( o.getId()) ) {
            return false;
        }

        long id_deleted = o.getId();
        String sql2 ="";
        try(var connection2 = DriverManager.getConnection(url, user, password)

        ) {
            sql2 = "Delete from \"order_product\" where order_id = " + id_deleted;
            var ps2 = connection2.prepareStatement(sql2);
            var rs2 = ps2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try(var connection = DriverManager.getConnection(url, user, password)

        ) {
            var ps = connection.prepareStatement(sql);
            var rs = ps.executeUpdate();

            if(rs > 0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrder(Long orderID, String status) {

        if(!orderExists(orderID) ){
            return false;
        }

        String sql ="update \"order\" SET "
                + "status = " + "'" + status + "'"
                + "where id = " + "'" + orderID + "'" +  ";";

        try (var connection = DriverManager.getConnection(url, user, password)

        ) {
            var ps = connection.prepareStatement(sql);
            var rs = ps.executeUpdate();

//            String sql2 = "Select * from \"order_product\" where order_id = " + id;
//
//            try (var connection2 = DriverManager.getConnection(url, user, password);
//                 var ps2 = connection2.prepareStatement(sql2);
//                 var rs2 = ps2.executeQuery()
//            ) {
//                List<Product> new_product_list = o.getProductList();
//                List<Product> what_to_add = new ArrayList<>();
//                List<Product> what_is_in_database = new ArrayList<>();
//
//                while(rs2.next()) {
//                    long product_id = rs2.getInt("product_id");
//                    Product p = getProduct(product_id);
//                    System.out.println("PRODUCT: " + p);
//
//                    what_is_in_database.add(p);
//                }
//
//                for(Product p: new_product_list) {
//                    if(!what_is_in_database.contains(p))
//                        what_to_add.add(p);
//                }
//
//                System.out.println("new product list: " + new_product_list);
//                System.out.println("what to add: " + what_to_add);
//
//                if(!what_to_add.isEmpty()) {
//                    for(Product p: what_to_add) {
//                        String sql3 = "insert into \"order_product\" " +
//                                " VALUES( " +
//                                "'" + id + "'" + "," +
//                                "'" + p.getId() + "');";


//                        try(var connection3 = DriverManager.getConnection(url, user, password)
//                        ) {
//                            var ps3 = connection3.prepareStatement(sql3);
//                            var rs3 = ps3.executeUpdate();
//
//                        }
            return true;
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean productExistsInOrderProduct(long productId) {
        String sql = "Select * from \"order_product\" ";

        try (var connection = DriverManager.getConnection(url, user, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
               long id = rs.getInt("product_id");
               if(id == productId)
                   return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
    /**
     * @param str The string for which we create a template,
     *            for adding into the database
     * @return the formatted string for sql
     */
    private static String createTemplate(String str) {
        StringBuilder tmp = new StringBuilder();
        tmp.append("'").append(str).append("'").append(", ");

        return tmp.toString();
    }

    /**
     * @return Returns a List of all the users in the
     * repository and loads the data from the database
     */
    public List<Order> getAll() {
        try {
            loadData();
            return new ArrayList<>(super.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(super.getAll());
    }




}
