package com.luckypeng.study.spark.sql

import com.luckypeng.study.spark.common.enumeration.{Age, Gender, Occupation}
import com.luckypeng.study.spark.common.model.User
import com.luckypeng.study.spark.common.utils.MovieEncoders._
import org.apache.spark.api.java.function.MapFunction
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.expressions.Aggregator
import org.junit.jupiter.api.Test

class TypedMax[IN](val f: IN => Double) extends Aggregator[IN, Double, Double] {
  override def zero: Double = 0.0
  override def reduce(b: Double, a: IN): Double = if (b > f(a)) b else f(a)
  override def merge(b1: Double, b2: Double): Double = if (b1 > b2) b1 else b2
  override def finish(reduction: Double): Double = reduction

  override def bufferEncoder: Encoder[Double] = ExpressionEncoder[Double]()
  override def outputEncoder: Encoder[Double] = ExpressionEncoder[Double]()

  // Java api support
  def this(f: MapFunction[IN, java.lang.Double]) = this(x => f.call(x).asInstanceOf[Double])

  def toColumnJava: TypedColumn[IN, java.lang.Double] = {
    toColumn.asInstanceOf[TypedColumn[IN, java.lang.Double]]
  }
}

case class SimpleUser(userId: Long, gender: String, age: Int, occupation: Int, zipCode: String)

class BasicOp {
  // 参考 org.apache.spark.sql.expressions.scalalang.typed 中的写法
  def max[IN](f: IN => Double): TypedColumn[IN, Double] = new TypedMax[IN](f).toColumn

  val spark = SparkSession
    .builder()
    .appName("BasicOp")
    .master("local[*]")
    .getOrCreate()
  import spark.implicits._
  val DATA_PATH = "../data/ml-1m"
  val OUTPUT_PATH = "target/outputs"

  /**
    * Dataset基本用法
    */
  @Test
  def testDataset(): Unit = {
    val userDS:Dataset[User] = spark.read.textFile(s"$DATA_PATH/users.dat")
      .map(_.split("::"))
      .map(p => User(p(0).toLong, Gender.withName(p(1)), Age(p(2).toInt), Occupation(p(3).toInt), p(4)))

    // 女性统计
    val fCount = userDS.filter(_.gender == Gender.F).count()
    println(s"fmale: $fCount")

    // 年龄分组统计
    userDS.groupByKey(_.age).count().foreach(t => println(s"age: ${t._1}, count: ${t._2}"))

    // 年龄排序
    userDS.rdd.sortBy(_.age.id).take(10).foreach(x => println(x.userId))

    // 邮编去重
    println(userDS.map(_.zipCode).distinct().count())

    // 性别分组，最大年龄
    userDS.groupByKey(_.gender).
      reduceGroups((a, b) => {if(a.age>b.age) a else b}).
      foreach(t => println(s"gender: ${t._1.toString}, max(age): ${t._2.age}"))

    // 分组聚合
    import org.apache.spark.sql.expressions.scalalang.typed.{count, sum}
    userDS.groupByKey(_.gender.toString).
      agg(sum[User](_.age.id).name("sum(age)"),
        count[User](_.occupation).name("count(occupation)"),
          max[User](_.age.id).name("max(age)")).
      withColumnRenamed("value", "gender").
      alias("Summary by user gender").
      show()

  }

  /**
    * SQL基本用法
    */
  @Test
  def testSql(): Unit = {
    val userDS:Dataset[SimpleUser] = spark.read.textFile(s"$DATA_PATH/users.dat")
      .map(_.split("::"))
      .map(p => SimpleUser(p(0).toLong, p(1), p(2).toInt, p(3).toInt, p(4)))

    userDS.toDF().createOrReplaceTempView("user")

    val limits:DataFrame = spark.sql("select * from user limit 10")
    limits.show()

    // 使用SQL进行统计
    val ageCount = spark.sql("select age,count(*) as total,count(distinct zipCode) as zip " +
      "from user group by age order by age")

    ageCount.show()

    // 结果写入文件
    ageCount.write.mode(SaveMode.Overwrite).json(s"${OUTPUT_PATH}/ageCount.json")
  }
}
