# Scala学习

## 基础知识
### 枚举
1. [枚举介绍](doc/basic/enum.md)

### 字符串

#### 相关文档
1. Scala官网: [字符串插值/String Interpolation](https://docs.scala-lang.org/zh-cn/overviews/core/string-interpolation.html)

### 操作符

### 控制语句

### 集合

#### 相关文档
1. Scala官网: [SCALA容器类体系结构](https://docs.scala-lang.org/zh-cn/overviews/core/architecture-of-scala-collections.html)
2. Scala官网: [容器简介](https://docs.scala-lang.org/zh-cn/overviews/collections/introduction.html)

### 隐式转换
1. 自动类型转换
2. 为已有的类库添加新功能
3. ```Predef```中定义的隐式转换为自动引入
4. 隐式参数
5. 隐式转换的规则
- 什么时候进行隐式转换？
  * 当方法中参数的类型与实际类型不一致
  * 当调用类中不存在的方法或成员时

- 什么时候不会进行隐式转换？
  * 可以不在隐式转换的编译通过
  * 转换存在二义性 -> 编译时会报错
  * 存在嵌套转换 -> 编译时会报错


## 课程/练习
### [Scala 课堂](http://twitter.github.io/scala_school/zh_cn/)

Scala课堂是Twitter启动的一系列讲座，用来帮助有经验的工程师成为高效的Scala 程序员。

### [《Scala编程》代码示例](https://www.cs.helsinki.fi/u/wikla/OTS/Sisalto/examples/index.html)
[《Scala编程》](https://union-click.jd.com/jdc?d=3RSpD6)，比[《快学Scala》](https://union-click.jd.com/jdc?d=rFvQnK)更好的在于不仅介绍了是什么，更加说明了为什么。

### [99个Scala问题](doc/ninety-nine-scala-problems/problems.md)
通过这些小问题的练习，可以让你熟悉Scala常用的操作。

* [List相关](doc/ninety-nine-scala-problems/Lists.md)


## 规范/建议
1. [Effective Scala](http://twitter.github.io/effectivescala/index-cn.html) Twitter的Scala最佳实践。对理解Twitter的代码风格非常有用。