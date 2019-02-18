package com.luckypeng.study.scala.basic

import org.junit.Test

/**
  * 控制语句
  */
class Controls {

  @Test
  def ef: Unit = {
    // if语句可以有返回值
    val a = if(true) "a" else "b"
    println(a.getClass.getSimpleName)
    assert(a == "a")
  }

  @Test
  def for4: Unit = {
    val list = List(1, 2, 3, 4)
    // 遍历
    for (l <- list) {
      println(l)
    }

    // 通过 yield 映射为新的集合
    val newList = for (i <- list if i % 2 == 0) yield i + 1
    println(newList)
  }

  /**
    * 匹配模式，类似java里的switch，只不过scala默认每个语句都是break的
    */
  @Test
  def matchCase: Unit = {
    val score = 70
    def result = score match {
      case n if n < 60 => "不及格"
      case n if n < 80 => "良"
      case _ => "优秀"
    }
    assert(result == "良")
  }
}
