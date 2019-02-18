package com.luckypeng.study.scala.basic

import org.junit.Test

/**
  * 操作符
  */
class Operators {

  @Test
  def test: Unit = {
    println(1+2)

    // 运算符的本质
    println(1.+(2))
  }

  @Test
  def equals: Unit = {
    // 这里的 == 比较的是值，和java不同
    println(List(1, 2) == List(1, 2))
    println(List(1, 2) equals List(1, 2))
  }
}
