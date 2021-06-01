name := "mssql-jdbc-krb5"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-client" % "2.6.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scala-lang" % "scala-library" % "2.11.2"
)
