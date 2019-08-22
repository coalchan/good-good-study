package com.luckypeng.study.spark.streaming.source

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

object QueueStreamTest {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("TestRDDQueue").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(20))
    val rddQueue =new scala.collection.mutable.SynchronizedQueue[RDD[Int]]()
    // oneAtATime 默认为 true，表示每个批次间隔只消费一个 RDD
    val queueStream = ssc.queueStream(rddQueue)
    val mappedStream = queueStream.map(r => (r % 10, 1))
    val reducedStream = mappedStream.reduceByKey(_ + _)
    reducedStream.print()
    ssc.start()
    for (i <- 1 to 10){
      rddQueue += ssc.sparkContext.makeRDD(1 to 100,2)
      Thread.sleep(1000)
    }
    ssc.awaitTermination()
  }
}
