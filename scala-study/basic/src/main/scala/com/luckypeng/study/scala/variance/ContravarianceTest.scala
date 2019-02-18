package com.luckypeng.study.scala.variance

/**
  * 根据里氏替换原则，所有使用父类型对象的地方都可以替换成子类型对象。
  * 下面这种情况下，
  * 如果 Printer[Animal] 是 Printer[Cat] 的子类型，则替换后 print 方法可以正常执行。
  * 反之，则替换后会引发不确定的问题。
  */
object ContravarianceTest extends App {
  val catPrinter: Printer[Cat] = new CatPrinter
  val animalPrinter: Printer[Animal] = new AnimalPrinter

  val myCat: Cat = Cat("Boot", 2)
  catPrinter.print(myCat)
  animalPrinter.print(myCat) // 可以执行

  val dog: Dog = Dog("Teddy")
  animalPrinter.print(dog)
//  catPrinter.print(dog) // 不能通过编译，如果可以的话，在执行CatPrinter.print的方法时也会报错
}
