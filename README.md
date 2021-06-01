MS SQL Server JDBC driver support for Sqoop and Spark (mssql-jdbc-krb5)
=======

MS Sql Server JDBC driver wrapper to allow kerberized connections for Sqoop/Spark on Yarn cluster. 

## MSSQL JDBC Driver using Kerberos (local)

Microsoft JDBC Driver for SQL Server supports using Kerberos integrated authentication to connect to SQL Server from single node linux machine.
[Docs](https://docs.microsoft.com/en-us/sql/connect/jdbc/using-kerberos-integrated-authentication-to-connect-to-sql-server?view=sql-server-ver15#using-kerberos-authentication-from-unix-machines-on-the-same-domain)

## MSSQL JDBC Driver using Kerberos (YARN cluster)
The driver works on local mode but while using the same driver for Sqoop or Spark jobs on YARN, we run into following issue

```
java.security.PrivilegedActionException: GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos tgt)
```
Integrated authentication does not work with SQLServer JDBC driver in a secure cluster with AD integration as the containers will not have the context as the kerberos tokens are lost when the mappers spawn (as the YARN transitions the job to its internal security subsystem).

## Get Started
This solution implements a Driver that overrides connect method of the latest MS SQL JDBC Driver (mssql-jdbc-9.2.1.jre8.jar), and will get a ticket for keytab file/principal, and gives this connection back.

You can grab the latest build of this custom driver from release folder 

```
mssql-jdbc-krb5_2.10-1.0.jar
```

Here is the example JDBC URL you should use:

"jdbc:krb5ss://<SERVER_NAME>:1433;databasename=<DATABASE_NAME>;integratedSecurity=true;authenticationScheme=JavaKerberos;krb5Principal=c795701@LA.CORP.CARGILL.COM;krb5Keytab=/efs/home/c795701/c795701.keytab"

### Sqoop

Sqoop command
```
export HADOOP_CLASSPATH=/efs/home/c795701/mssql-jdbc-krb5/target/scala-2.10/mssql-jdbc-krb5_2.10-1.0.jar:/efs/home/c795701/.ivy2/jars/scala-library-2.11.1.jar
```

```
sqoop import -libjars "/efs/home/c795701/mssql-jdbc-krb5/target/scala-2.10/mssql-jdbc-krb5_2.10-1.0.jar,/efs/home/c795701/.ivy2/jars/scala-library-2.11.1.jar" \
-files "/efs/home/c795701/c795701.keytab" \
--connection-manager org.apache.sqoop.manager.SQLServerManager \
--driver hadoop.sqlserver.jdbc.krb5.SQLServerDriver \
--connect "jdbc:krb5ss://<SERVER_NAME>:1433;databasename=<DATABASE_NAME>;integratedSecurity=true;authenticationScheme=JavaKerberos;krb5Principal=c795701@LA.CORP.CARGILL.COM;krb5Keytab=/efs/home/c795701/c795701.keytab" \
--query "SELECT TOP 1000 * FROM <TABLE_NAME> WHERE \$CONDITIONS" \
--delete-target-dir \
--target-dir "/dev/product/sandbox/<table_name>" \
--num-mappers 1 \
--verbose \
-- --schema "dbo"
```

## Sqoop with Kerberos Authentication
Sqoop is a wrapper for the creation and submission of MapReduce jobs (and everything here is with respect to MRv2 on YARN).
The kerberos tokens are lost when the mappers spawn (as the YARN transitions the job to its internal security subsystem).

When sqoop import is invoked from the client, resources for the associated Map tasks are allocated by the YARN Resource Manager in the form of YARN containers that are scheduled to run on the cluster's worker nodes. These containers are not forwarded Kerberos tickets, instead they are given delegation tokens (requested by the client and associated with the container's launch context). More details are available [here](https://github.com/steveloughran/kerberos_and_hadoop/blob/master/sections/hadoop_tokens.md#example) and [here](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=31822268).

These delegation tokens can be used to access Hadoop resources, such as HDFS, under the authorization context of the client. There is a speculation that since the execution context for the Map tasks does not contain the original TGT, this poses an issue for authenticating via Kerberos to services outside of Hadoop that don't interact with Hadoop delegation tokens, like SQL Server.

## Hadoop and Kerberos: The Madness Beyond the Gate
Hadoop can use Kerberos to authenticate users, and processes running within a Hadoop cluster acting on behalf of the user. It is also used to authenticate services running within the Hadoop cluster itself -so that only authenticated HDFS Datanodes can join the HDFS filesystem, that only trusted Node Managers can heartbeat to the YARN Resource Manager and receive work.

Hadoop complicates things by adding another form of delegated authentication, Hadoop Tokens. [More details](https://steveloughran.gitbooks.io/kerberos_and_hadoop/content/sections/hadoop_tokens.html)
However, anyone attempting to read this will generally come out with at least a light headache and no better informed.


## Contributors
Special thanks to Grzegorz Caban who has contributed to initial work on this project.

This repository was enhanced from the work of Grzegorz Caban at https://github.com/nabacg/krb5sqljdb

Alternatively, you can also use the JTDS driver, which supports the NTLM authentication.