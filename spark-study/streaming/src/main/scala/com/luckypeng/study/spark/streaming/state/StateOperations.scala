package com.luckypeng.study.spark.streaming.state

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit.jupiter.api.Test

class StateOperations {
  /**
    * updateStateByKey
    * 核心参数为一个更新状态函数：
    * 该函数接受两个参数：
    *   1. 新到的数据，封装成了序列
    *   2. 状态值
    */
  @Test
  def testUpdateStateByKey: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    // updateStateByKey 操作需要 checkpoint
    ssc.checkpoint("checkpoint-dir")

    val lines = ssc.socketTextStream("localhost", 9999)

    val wordsCounts = lines
      .flatMap(_.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    // 只累加出现次数在 2 次及以上的单词
    val func = (values:Seq[Int], counts: Option[Int]) => {
      val newCount = counts.getOrElse(0) + values.filter(x => x >= 2).sum
      if (newCount > 0)
        Some(newCount)
      else
        None
    }

    val result = wordsCounts.updateStateByKey(func)

    result.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
