package com.luckypeng.study.spark.streaming.window

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit.jupiter.api.Test

class WindowOperations {
  /**
    * reduceByWindow
    */
  @Test
  def testReduceByWindow: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.socketTextStream("localhost", 9999)
    lines.map(_.length).reduceByWindow(_ + _, Seconds(60), Seconds(15)).print()

    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * reduceByKeyAndWindow
    * 针对 (key, value) 形式的 Stream，根据 key 进行 reduce
    */
  @Test
  def testReduceByKeyAndWindow: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.socketTextStream("localhost", 9999)

    val words = lines.flatMap(_.split(" "))

    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKeyAndWindow(_ + _, Seconds(30))

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
