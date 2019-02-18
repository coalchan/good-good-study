package com.luckypeng.study.spark.common.utils

import com.luckypeng.study.spark.common.enumeration.Age.Age
import com.luckypeng.study.spark.common.enumeration.Gender.Gender
import com.luckypeng.study.spark.common.model.User
import org.apache.spark.sql.Encoders

object MovieEncoders {
  implicit val userEncoder =  Encoders.kryo[User]
  implicit val ageEncoder =  Encoders.kryo[Age]
  implicit val genderEncoder =  Encoders.kryo[Gender]
}
