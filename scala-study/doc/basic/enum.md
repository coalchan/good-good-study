# 枚举

Scala中没有提供一个枚举类型，但是标准库中提供了一个```Enumeration```的抽象类，通过继承实现一个枚举。

例:
```scala
object Color extends Enumeration {
  type Color = Value // 2
  val RED,BLUE,GREEN = Value // 1
}
```

1. ```Value```是```Enumeration```的内部类，调用它们可以返回一个对应的新实例，因此枚举的类型实际上为```Color.Value```;
2. 这里增加一个别名，因而枚举的类型实际上变成了```Color.Color```

这样的话，就可以如下方式使用：
```scala
import Color.Color
def isRed(color: Color) = color == Color.RED
```

此外，```Value```支持ID、名称，或者两个参数都传:
```scala
val RED = Value(1, "RED")
val BLUE = Value(2)
val GREEN = Value("GREEN")
```

最后，可以通过ID或者名称来进行枚举类型的查找：
```scala
Color(1)
Color.withName("RED")
```