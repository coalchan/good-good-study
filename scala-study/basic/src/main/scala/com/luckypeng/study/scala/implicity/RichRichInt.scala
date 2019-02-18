package com.luckypeng.study.scala.implicity

/**
  * 通过隐式转换，快速地扩展现有类库的功能
  * 仿照[[scala.runtime.RichInt]]，给Int增加一个方法
  * @param self
  */
class RichRichInt(val self: Int) {
  def addOne: Int = self + 1
}

object RichRichInt {
  implicit def int2RichRich(x: Int) = new RichRichInt(x)

  def main(args: Array[String]): Unit = {
    val x = 19
    println(x.addOne)
    assert(x.getClass == classOf[Int])
  }
}
