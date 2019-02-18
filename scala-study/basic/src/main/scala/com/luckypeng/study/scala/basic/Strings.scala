package com.luckypeng.study.scala.basic

import org.junit.Test

/**
  * 字符串
  */
class Strings {

  @Test
  def test: Unit = {
    val str1 = "中国牛逼"
    println(str1)

    // 字符串插值
    val str2 = s"${str1}是真的"
    println(str2)

    // 不用转义，常用于正则表达式，因为比较简练
    val str3 = s"""不用想，\n${str1}是真的"""
    println(str3)
  }


}
