package com.loganalyzer.utils

import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {
  val validDatePattern = "MMM dd HH:mm:ss"
  val dateFormat = new SimpleDateFormat(validDatePattern)
  val months = List("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
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

  def handleDateFormat(value: String): Date = {
    // default parsing doesn't work for invalid suffix
    if (!isValidDateFormat(value)) {
      throw new Exception("Only valid date format: " + validDatePattern)
    }

    val date = dateFormat.parse(value)
    date
  }

  def isValidDateFormat(value: String): Boolean = {
    val dateSplits = value.split(' ')
    if (dateSplits.length == 3 && months.contains(dateSplits(0)) && dateSplits(1).toInt <= 12) {
      val timeSplits = dateSplits(2).split(':')
      if (timeSplits.length == 3 && timeSplits(0).toInt < 24 &&
          timeSplits(1).toInt < 60 && timeSplits(2).toInt < 60) {
        return true
      }
    }
    false
  }
}
