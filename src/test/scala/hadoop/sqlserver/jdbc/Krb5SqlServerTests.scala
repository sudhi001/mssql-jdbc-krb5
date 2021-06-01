package hadoop.sqlserver.jdbc

import org.scalatest.{FreeSpec, Matchers}

/**
 * Created by IntelliJ IDEA.
 *
 * @author : Chandan Balu
 * @created_date : 6/1/2021, Tue
 * */

class Krb5SqlServerTests extends FreeSpec with Matchers{

  "extract correct properties from jdbc url" in {
    val url = "jdbc:sqlserver://serverName:1023;integratedSecurity=true;authenticationScheme=JavaKerberos"

    KerberosDriver.connectionProperties(url) should be(Map("integratedSecurity"->"true", "authenticationScheme"->"JavaKerberos"))

  }

  "convert to krb to sql server url" in {
    val principal = "testUser"
    val keytab = "testUser.keytab"
    val url =  "jdbc:sqlserver://serverName:1023;integratedSecurity=true;authenticationScheme=JavaKerberos;"
    val krbUrl = s"jdbc:${KerberosDriver.krbPrefix}://serverName:1023;integratedSecurity=true;authenticationScheme=JavaKerberos;${KerberosDriver.principalKey}=$principal;${KerberosDriver.keytabFile}=$keytab"

    KerberosDriver.toSqlServerUrl(krbUrl) should be(url)
  }

  "get principal and keytab path from url" in {
      val principal = "testUser"
      val keytab = "testUser.keytab"

      val krbUrl = s"jdbc:${KerberosDriver.krbPrefix}://serverName:1023;${KerberosDriver.principalKey}=$principal;${KerberosDriver.keytabFile}=$keytab"
      val props = KerberosDriver.connectionProperties(krbUrl)

      props.get(KerberosDriver.principalKey) should be(Some(principal))
      props.get(KerberosDriver.keytabFile) should be(Some(keytab))

  }
}
