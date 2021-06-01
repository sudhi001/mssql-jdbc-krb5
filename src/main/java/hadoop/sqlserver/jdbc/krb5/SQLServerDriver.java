package hadoop.sqlserver.jdbc.krb5;

import hadoop.sqlserver.jdbc.KerberosDriver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author : Chandan Balu
 * @created_date : 6/1/2021, Tue
 **/
public class SQLServerDriver extends KerberosDriver {
/**
 * A Driver class that extends KerberosDriver to provide the SQL Server connection when
 * DriverManager.getConnection(String url) method of Java DriverManager class attempts to establish
 * a connection to the database by using the given database URL.
 *
 * The appropriate driver from the set of registered JDBC drivers is selected -
 * so we'll be calling to register this custom driver and it will be invoked.
 *
 * https://docs.oracle.com/javase/8/docs/api/java/sql/DriverManager.html?is-external=true
 */
    // This static block inits the driver when the class is loaded by the JVM.
    static {
        try {
            DriverManager.registerDriver(new SQLServerDriver());
        } catch (SQLException e) {
            throw new RuntimeException( "Failed to register SQLServerDriver: " + e.getMessage());
        }
    }
}
