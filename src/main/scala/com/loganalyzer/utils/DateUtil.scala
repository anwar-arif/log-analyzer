package com.loganalyzer.utils

import org.slf4j.LoggerFactory

import java.sql.Date
import java.text.SimpleDateFormat

object DateUtil {
  val dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss")
  val logger = LoggerFactory.getLogger("DateUtil")

  def getEpoch(time: String): Long = {
    try {
      dateFormat.parse(time).getTime
    } catch {
      case ex: Exception => {
        logger.error(s"Can't parse date: $time: " + ex.getMessage)
        throw new Exception("Custom exception")
      }
    }
  }

  def getDate(epoch: Long): String = {
    try {
      dateFormat.format(new Date(epoch))
    } catch {
      case ex: Exception => {
        logger.error(s"Couldn't convert $epoch to date: " + ex.getMessage)
        throw new Exception("Couldn't convert date")
      }
    }
  }
}
