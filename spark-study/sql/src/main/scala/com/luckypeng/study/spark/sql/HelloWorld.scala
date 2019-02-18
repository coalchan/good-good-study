package com.luckypeng.study.spark.sql

import org.apache.spark.sql.SparkSession


object HelloWorld {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
        .builder()
        .appName("SparkSQLSimpleExample")
        .master("local")
        .getOrCreate()
    import spark.implicits._
    val userDS = spark.createDataset(List(1,2,3))
    userDS.show(3)
  }
}
