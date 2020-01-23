name := "SomeDataprocSparkJob"

version := "1.0"

scalaVersion := "2.11.12"

val sparkVersion = "2.4.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
  ,"org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
  ,"org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"
  ,"com.google.cloud.sql" % "mysql-socket-factory-connector-j-8" % "1.0.15"
  ,"mysql" % "mysql-connector-java" % "8.0.18"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}