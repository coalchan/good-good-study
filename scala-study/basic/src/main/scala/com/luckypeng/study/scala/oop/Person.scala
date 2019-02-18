package com.luckypeng.study.scala.oop

class Person () {
  private var _name: String = _

  def name = _name
  def name_= (newName: String) = {
    _name = newName
  }
}
