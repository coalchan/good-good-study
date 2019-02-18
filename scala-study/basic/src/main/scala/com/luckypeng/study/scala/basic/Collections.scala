package com.luckypeng.study.scala.basic

import org.junit.Test

/**
  * 集合
  */
class Collections {

  /**
    * ::操作符的作用是将一个元素和列表连接起来，并把元素放在列表的开头
    */
  @Test
  def list: Unit = {
    val list = 1 :: 2 :: 3 :: Nil
    println(list)

    list.filter(i => i==3).foreach(i => println(i))
  }

  /**
    * 可以使用xxx._[X]的形式来引用Tuple中某一个具体元素，其_[X]下标是从1开始的
    */
  @Test
  def tuple: Unit = {
    val tuple1 = (1, 2, 5)
    println(tuple1._3)
  }

  /**
    * Set是一个不重复且无序的集合
    */
  @Test
  def set: Unit = {
    val set = Set("Python", "Java", "Scala", "JS", "JAVA")
    println(set)
    println(set + "C#" + "C++")
  }

  /**
    * Map为不可变集合，如果需要可变集合，需要import scala.collection.mutable.Map
    */
  @Test
  def map: Unit = {
    val map = Map("a" -> "b", "c" -> 1, 2 -> 3)
    println(map)
    println(map + ("d" -> "f"))
  }
}
