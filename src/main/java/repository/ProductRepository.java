package repository;

import domain.Admin;
import domain.Customer;
import domain.Product;
import domain.exception.CustomException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: The ProductRepository class, represents the interface of the application
 * with the database, everything you want to modify in the database related to
 * the products, will be done through this repo
 */

public class ProductRepository extends AbstractRepository<Long, Product>{

    private static String url;
    private static String username;
    private static String password;

    /**
     * Database Credentials
     * @param url url of the database
     * @param username ursername of the database
     * @param password password for the database
     */
    public ProductRepository(String url, String username, String password) {
        ProductRepository.url = url;
        ProductRepository.username = username;
        ProductRepository.password = password;
    }

    /**
     * This function loads data from the database into the memory
     * @throws SQLException If there is the case, there will be thrown
     *a SqlException while using the database
     */
    private void loadData() throws SQLException {

        super.elems.clear();
        String sql = "Select * from \"product\" ";

        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()) {

                while( rs.next() ) {

                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    String ingredients = rs.getString("ingredients");
                    double price = rs.getDouble("price");
                    String expirationDate = rs.getString("expirationdate");

                    Product product = new Product(name, ingredients, price, expirationDate);
                    product.setId(id);
                    super.add(product);
                }
        } catch (SQLException | CustomException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that checks the existance of a product
     * @param name The product's name
     * @return true if the product exists in the database,
     * false otherwise
     */
    public boolean productExists(String name) {

        String sql = "Select * from \"product\" ";

        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                String Name = rs.getString("name");
                if(Name.equals(name))
                    return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param p The product that needs to be added in the database
     * @return true if the product hass been added, false otherwise
     * @throws CustomException Duplicated product CustomException, if
     * there product already exists in the database
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     */
    public boolean addProduct(Product p) throws CustomException, SQLException {

        String sql;

        String name = p.getName();
        String ingredients = p.getIngredients();
        String expirationDate = p.getExpirationDate();
        double price = p.getPrice();


        //TODO: Implement the duplicated message into the GUI,
        // instead of using an exception
        int new_id = super.elems.size() + 1;
        System.out.println("NEW_iD: " + new_id);

        if( productExists(p.getName()) ) {
            return false;
        }

        try (var connection = DriverManager.getConnection(url, username, password)

        ) {
            sql = "insert into \"product\" "
                    + "VALUES(" + "'" + new_id + "'"  + ", "
                    + createTemplate(name)
                    + createTemplate(ingredients)
                    + "'" + price + "'" + ", "
                    + "'" + expirationDate + "'"
                    + ");";

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
     * @param p The product for which we search the id
     * @return the id(long > 0) if the product was found,
     * -1 otherwise
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     */
    public long findProductId(Product p) throws SQLException {

        String sql = "Select * from \"product\" where " +
                "name = " + "'" + p.getName() + "';";

        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(sql);
             var rs = ps.executeQuery()
        ) {

            while( rs.next() ) {
                long id = -1;
                id = rs.getLong("id");
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
    public boolean updateIndexes(long id) {
        String sql = "update \"product\" SET id = id - 1" +
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
     * @param p The product which needs to be deleted from
     *          the database
     * @return true if the product was deleted, false otherwise
     * @throws CustomException Product doesn't exist in the database,
     * CustomException
     * @throws SQLException If there is the case, there will be thrown
     * a SqlException while using the database
     */
    public boolean deleteProduct(Product p) throws CustomException, SQLException {

        long idFromWhereToUpdate = findProductId(p);
        if(idFromWhereToUpdate < 0)
            return false;

        if( productExists(p.getName()) ) {
            String sql = "delete from \"product\" " +
                    "where name = " + "'" +
                    p.getName() + "';";

            try (var connection = DriverManager.getConnection(url, username, password)
            ) {
                var ps = connection.prepareStatement(sql);
                var rs = ps.executeUpdate();

                //deleting from the memory repository
                super.elems.remove(p.getId());
                if( updateIndexes(idFromWhereToUpdate) ) {
                    return true;
                }
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            return false;
        }
        return false;
    }

    /**
     * @param pUpdated The new product, already updated
     * @return true if the product was updated, false otherwise
     * @throws SQLException If there is the case, there will be thrown
     *a SqlException while using the database
     */
    public boolean updateProduct(Product pUpdated) throws SQLException {

        String sql = "update \"product\"  SET "
                + "ingredients = " + createTemplate(pUpdated.getIngredients())
                + "price = " + "'" + pUpdated.getPrice() + "', "
                + " expirationdate = " + "'" + pUpdated.getExpirationDate() + "'"
                + "where name = " + "'" + pUpdated.getName() + "'" + ";";

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
     * @return The list with all the products in the
     * repository
     */

    public static Product getProduct(Long productId) {
        String productSql = "select * from product where id=" + productId;
        try (var connection = DriverManager.getConnection(url, username, password);
             var ps = connection.prepareStatement(productSql);
             var rs = ps.executeQuery()) {

            rs.next();

            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String ingredients = rs.getString("ingredients");
            double price = rs.getDouble("price");
            String exp = rs.getString("expirationdate");

            Product product = new Product(name, ingredients, price, exp);
            product.setId(id);

            return product;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAll() {
        try {
            loadData();
            return new ArrayList<>(super.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(super.getAll());
    }
}
