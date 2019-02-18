package com.luckypeng.study.spark.common.enumeration

object Occupation extends Enumeration {
  type Occupation = Value
  val OTHER = Value(0, "other or not specified")
  val ACADEMIC = Value(1, "academic/educator")
  val ARTIST = Value(2, "artist")
  val CLERICAL = Value(3, "clerical/admin")
  val COLLEGE = Value(4, "college/grad student")
  val CUSTOMER = Value(5, "customer service")
  val DOCTOR = Value(6, "doctor/health care")
  val EXECUTIVE = Value(7, "executive/managerial")
  val FARMER = Value(8, "farmer")
  val HOMEMAKER = Value(9, "homemaker")
  val STUDENT = Value(10, "K-12 student")
  val LAWYER = Value(11, "lawyer")
  val PROGRAMMER = Value(12, "programmer")
  val RETIRED = Value(13, "retired")
  val SALES = Value(14, "sales/marketing")
  val SCIENTIST = Value(15, "scientist")
  val SELF = Value(16, "self-employed")
  val TECHNICIAN = Value(17, "technician/engineer")
  val TRADESMAN = Value(18, "tradesman/craftsman")
  val UNEMPLOYED = Value(19, "unemployed")
  val WRITER = Value(20, "writer")
}
