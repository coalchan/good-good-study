package com.luckypeng.study.scala.implicity

import org.junit.Test

/**
  * 隐式转换
  */
class HelloWorld {
  @Test
  def test: Unit = {
    implicit def double2Int(x: Double): Int = x.toInt
    val x:Int = 1.2
    println(x)
  }
}
