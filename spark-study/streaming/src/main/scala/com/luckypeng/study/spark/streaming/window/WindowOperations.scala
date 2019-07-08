package com.luckypeng.study.spark.streaming.window

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit.jupiter.api.Test

class WindowOperations {
  /**
    * reduceByWindow
    * 对 value 进行 reduce
    */
  @Test
  def testReduceByWindow: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.socketTextStream("localhost", 9999)
    lines.map(_.length).reduceByWindow(_ + _, Seconds(30), Seconds(10)).print()

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

  /**
    * countByWindow
    * 统计次数
    */
  @Test
  def testCountByWindow: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    // countByWindow 操作需要 checkpoint
    ssc.checkpoint("checkpoint-dir")

    val lines = ssc.socketTextStream("localhost", 9999)
    lines.countByWindow(Seconds(30), Seconds(10)).print()

    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * countByValueAndWindow
    * 对 value 进行分组统计，输出格式为 (value, count)
    */
  @Test
  def testCountByValueAndWindow: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    // countByValueAndWindow 操作需要 checkpoint
    ssc.checkpoint("checkpoint-dir")

    val lines = ssc.socketTextStream("localhost", 9999)
    lines.countByValueAndWindow(Seconds(30), Seconds(10)).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
