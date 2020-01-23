package dev.ancor.somedataprocsparkjob

import org.apache.spark.sql.SparkSession

object SomeSparkJob {

  def main(args: Array[String]): Unit = {
    val sparkSession =
      if (args.headOption.map(_.toLowerCase).contains("prod")) {
        SparkSession
          .builder()
          .appName("SomeSparkJob")
          .getOrCreate()
      } else {
        SparkSession
          .builder()
          .appName("SomeSparkJob")
          .config("spark.master", "local")
          .getOrCreate()
      }

    // sparkSession.conf.getAll.foreach(println)
    sparkSession.read
      .format("jdbc")
      .options(Map(
        "url" -> "jdbc:mysql:///YOUR_DB?cloudSqlInstance=YOUR_PROJECT_ID:us-central1:YOUR_INSTANCE_NAME&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&user=YOUR_USER&password=YOUR_PWD&useUnicode=true&characterEncoding=UTF-8",
        "dbtable" -> "YOUR_TABLE"
      ))
      .load()
      .show(1)

    sparkSession.stop()
  }

}
