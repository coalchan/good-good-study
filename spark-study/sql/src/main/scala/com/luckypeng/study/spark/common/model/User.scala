package com.luckypeng.study.spark.common.model

import com.luckypeng.study.spark.common.enumeration.Gender.Gender
import com.luckypeng.study.spark.common.enumeration.Age.Age
import com.luckypeng.study.spark.common.enumeration.Occupation.Occupation

case class User(userId: Long,
                gender: Gender,
                age: Age,
                occupation: Occupation,
                zipCode: String)
