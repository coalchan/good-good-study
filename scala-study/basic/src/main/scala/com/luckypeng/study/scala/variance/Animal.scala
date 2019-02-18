package com.luckypeng.study.scala.variance

abstract class Animal {
  def name: String
}

case class Cat(name: String, age: Int) extends Animal
case class Dog(name: String) extends Animal
