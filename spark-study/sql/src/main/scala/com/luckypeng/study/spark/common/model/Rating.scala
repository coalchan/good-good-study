package com.luckypeng.study.spark.common.model

case class Rating (userId: Long,
                   movieId: Long,
                   rating: Int,
                   timestamp: Long)
