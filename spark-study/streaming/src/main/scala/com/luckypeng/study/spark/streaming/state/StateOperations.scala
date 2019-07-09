package com.luckypeng.study.spark.streaming.state

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
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

  /**
    * mapWithState
    */
  @Test
  def testMapWithState: Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    // mapWithState 操作需要 checkpoint
    ssc.checkpoint("checkpoint-dir")

    val lines = ssc.socketTextStream("localhost", 9999)

    val wordsCounts = lines
      .flatMap(_.split(" "))
      .map(word => (word, 1))

    // 3 个参数分别为 key, value, state
    val func = (word: String, count: Option[Int], state: State[Int]) => {
      println("key: " + word + ", count: " + count)
      if (state.isTimingOut()) {
        println(word + " is timing out: " + state.get())
      }
      else {
        val sum = count.getOrElse(0) + (if (state.exists()) state.get() else 0)
        state.update(sum)
        (word, count)
      }
    }

    val result = wordsCounts.mapWithState(StateSpec.function(func).timeout(Seconds(10)))

    // 打印当前值
    result.print()

    // 获取全部 state 的值，并打印
    result.stateSnapshots().print()

    ssc.start()
    ssc.awaitTermination()
  }
}
