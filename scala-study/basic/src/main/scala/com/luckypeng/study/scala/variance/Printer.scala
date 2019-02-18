package com.luckypeng.study.scala.variance

abstract class Printer[-A] {
  def print(value: A): Unit
}

class AnimalPrinter extends Printer[Animal] {
  override def print(value: Animal): Unit =
    println(s"The Animal's name is ${value.name}")
}

class CatPrinter extends Printer[Cat] {
  override def print(value: Cat): Unit =
    println(s"The cat's name is ${value.name}, and its age is ${value.age}")
}
