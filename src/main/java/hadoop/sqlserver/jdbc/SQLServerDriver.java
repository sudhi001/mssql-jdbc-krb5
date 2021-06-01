package hadoop.sqlserver.jdbc.krb5;

import hadoop.sqlserver.jdbc.KerberosDriver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by cab on 06/01/2016.
 *
 */
public class SQLServerDriver extends KerberosDriver {

    // This static block inits the driver when the class is loaded by the JVM.
    static {
        try {
            DriverManager.registerDriver(new SQLServerDriver());
        } catch (SQLException e) {
            throw new RuntimeException( "Failed to register SQLServerDriver: " + e.getMessage());
        }
    }
}
