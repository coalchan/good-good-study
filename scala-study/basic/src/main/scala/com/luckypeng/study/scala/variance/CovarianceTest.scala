package com.luckypeng.study.scala.variance

object CovarianceTest extends App {
  def printAnimalName(animals: List[Animal]): Unit = {
    animals.foreach{
      animal => println(animal.name)
    }
  }

  val cats = List(Cat("Whiskers", 1), Cat("Tom", 2))
  val dogs = List(Dog("Fido"), Dog("Rex"))

  printAnimalName(cats)
  printAnimalName(dogs)
}
