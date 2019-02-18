package com.luckypeng.study.scala.variance

class Container[A](value: A) {
  private var _value: A = value
  def getValue: A = value
  def setValue(value: A): Unit = {
    _value = value
  }
}

object Container extends App {
  val catContainer: Container[Cat] = new Container(Cat("Felix", 1))
//  val animalContainer: Container[Animal] = catContainer
//  animalContainer.setValue(Dog("Spot"))
  val cat: Cat = catContainer.getValue
}
