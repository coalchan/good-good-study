package com.luckypeng.study.spark.streaming

import org.apache.spark._
import org.apache.spark.streaming._

object HelloWorld {
  /**
    * local monitor UI: http://localhost:4040
    * @param args
    */
  def main(args: Array[String]): Unit = {
    // Create a local StreamingContext with two working thread and batch interval of 1 second.
    // The master requires 2 cores to prevent a starvation scenario.
    // 注意，这里用的是 local[2]，本地运行时一个线程将用于接收数据，一个线程用于处理数据，要保证的是线程数要大于 Receiver 个数
    // 详情请参考 http://spark.apache.org/docs/latest/streaming-programming-guide.html#input-dstreams-and-receivers
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(1))

    // Create a DStream that will connect to hostname:port, like localhost:9999. ps: DStream[String]
    val lines = ssc.socketTextStream("localhost", 9999)

    // Split each line into words
    val words = lines.flatMap(_.split(" "))

    // Count each word in each batch
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print()

    // Start the computation
    ssc.start()

    // Wait for the computation to terminate
    ssc.awaitTermination()
  }
}