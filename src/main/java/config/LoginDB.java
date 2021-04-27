package config;
import java.sql.*;

public class LoginDB {

    /**
     * @param username The username entered by the user
     * @param password The pw entered by the user
     * @return It returns a boolean value, true if the LoginData is correct
     * and false otherwise
     */
    public static boolean checkLoginData(String username, String password) {

        String connectionUrl = "jdbc:postgresql://tai.db.elephantsql.com:5432/ktlzkben";

        try(
                Connection conn = DriverManager.getConnection(connectionUrl,"ktlzkben", "U85A51ME0gW4yVaPXdY--oJgSCm313Rn");
                Statement stmt = conn.createStatement();
        ){
            /*
             * Requesting all the usernames and passwords from
             * the database so we can check whether the login data
             * received is correct
             */

            String selectStr = "select USERNAME, PASSWORD from UserTable";

            ResultSet res = stmt.executeQuery(selectStr);

            while(res.next()) {
                String user = res.getString("USERNAME");
                String Password = res.getString("PASSWORD");

                if(password.equals(Password) &&
                        username.equals(user) )
                    return true;
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
