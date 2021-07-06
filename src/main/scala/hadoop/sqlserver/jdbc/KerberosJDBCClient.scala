package hadoop.sqlserver.jdbc

import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.ResultSet
import java.util
import java.util.Properties

/**
 * Created by IntelliJ IDEA.
 *
 * @author : Chandan Balu
 * @created_date : 6/1/2021, Tue
 * */

object KerberosJDBCClient {

  def main(args: Array[String]): Unit = {

    //String connUrl = "jdbc:sqlserver://SERVERNAME:1433;databaseName=DATABASENAME;integratedSecurity=true;authenticationScheme=JavaKerberos";

    val connUrl = "jdbc:krb5ss://SERVERNAME:1433;databaseName=DATABASENAME;integratedSecurity=true;authenticationScheme=JavaKerberos;krb5Principal=c795701@DOMAINNAME;krb5Keytab=/efs/home/c795701/c795701.keytab"

    System.out.println("Starting the Kerberos JDBC Client")

    try {
      //Class.forName("hadoop.sqlserver.jdbc.krb5.SQLServerDriver");
      try {
        Class.forName("hadoop.sqlserver.jdbc.krb5.SQLServerDriver").newInstance
        System.out.println("Driver already registered")
      } catch {
        case e: Exception =>
          System.out.println(e.toString)
      }

      System.out.println("List of all the Drivers registered with the DriverManager:")
      //Retrieving the list of all the Drivers
      val e = DriverManager.getDrivers
      //Printing the list
      while ( {
        e.hasMoreElements
      }) System.out.println(e.nextElement.getClass)

      val info = new Properties()
      info.setProperty("mode", "Local")
      val conn = DriverManager.getConnection(connUrl, info)

      val statement = conn.createStatement
      val resultSet = statement.executeQuery("SELECT COUNT(*) FROM (SELECT TOP 100 * FROM dbo.Global_FinishedProducts) A")
      while ( {
        resultSet.next
      }) System.out.println("Total row counts: " + resultSet.getString(1))
    } catch {
      case exception: Exception =>
        exception.printStackTrace()
    }
  }
}
