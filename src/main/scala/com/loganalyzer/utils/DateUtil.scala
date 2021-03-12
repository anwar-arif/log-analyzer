package com.loganalyzer.utils

import org.slf4j.LoggerFactory

import java.sql.Date
import java.text.SimpleDateFormat

object DateUtil {
  val dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss")
  val logger = LoggerFactory.getLogger("DateUtil")

  def getEpoch(time: String): Long = dateFormat.parse(time).getTime
  def getDate(epoch: Long): String = dateFormat.format(new Date(epoch))
}
