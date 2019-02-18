package com.luckypeng.study.spark.common.enumeration

object Age extends Enumeration {
  type Age = Value
  val AGE_1 = Value(1, "Under 18")
  val AGE_18 = Value(18, "18-24")
  val AGE_25 = Value(25, "25-34")
  val AGE_35 = Value(35, "35-44")
  val AGE_45 = Value(45, "45-49")
  val AGE_50 = Value(50, "50-55")
  val AGE_56 = Value(56, "56+")
}
