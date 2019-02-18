# 目标
这里主要是掌握对于List的用法，以及内部原理的理解，甚至尝试自己去优化常用方法。

# 涉及知识点
1. 偏函数

在Scala中一切皆对象，偏函数是```PartialFunction```，与之对立的的是全函数```FunctionX```（```X```从0到22）。
偏函数最常见的形式应该是```case```序列，主要应用在我们确信不会有不能处理的值传入。
如这里有一个返回第二个元素的偏函数
```scala
val second: List[Int] => Int = {
  case x :: y :: _ => y
}
```
在编译时会发出警告:
```scala
<console>:11: warning: match may not be exhaustive.
It would fail on the following inputs: List(_), Nil
```
如果传入2个元素及以上的列表，则正常返回，否则会报错：
```scala
scala> second(List(1, 4, 5))
res16: Int = 4

scala> second(List(1))
scala.MatchError: List(1) (of class scala.collection.immutable.$colon$colon)
  at .$anonfun$second$1(<console>:11)
  at .$anonfun$second$1$adapted(<console>:11)
  ... 28 elided
```
事实上，```case```序列语句会被Scala编译器编译成偏函数，例如以上的```second```函数会被编译成以下形式：
```scala
val second = new PartialFunction[List[Int], Int] {
  override def isDefinedAt(x: List[Int]): Boolean = x match {
    case x :: y :: _ => true
    case _ => false
  }

  override def apply(v1: List[Int]): Int = v1 match {
    case x :: y :: _ => y
  }
}
```

2. 操作符

由于scala中一切操作都是方法调用，当一个方法调用以操作符的形式出现的时候，比如:

```a * b```表示的其实是```a.*(b)```，即方法的调用默认发生在左操作元（left operand）上。
但是，如果操作符以冒号（```:```）结尾，则该方法的调用发生在右操作元（right operand）上。
例如```1 :: Nil```表示的是```Nil.::(1)```