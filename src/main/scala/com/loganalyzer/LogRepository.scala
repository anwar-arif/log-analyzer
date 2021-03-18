package com.loganalyzer

import com.loganalyzer.Models.DataModel._
import com.loganalyzer.utils.{DateUtil, FileUtil}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.sql.{Connection, DriverManager}

object LogRepository {
  var connection: Option[Connection] = None
  val logger = LoggerFactory.getLogger("LogRepository")
  var dbUrl: String = ""
  val (tableName, dateCol, messageCol) = ("logs", "date", "message")

  def getConnection(): Option[Connection] = {
    connection match {
      case Some(_) => connection
      case None => {
        initializeDB()
        LogReader.readLogData()
        connection
      }
    }
  }

  private def initializeDB(): Option[Connection] = {
    val config = ConfigFactory.load("application.conf")
    val connectionString = config.getString("app.sqlite.connectionString")
    val dbFilePath = FileUtil.getDbFilePath()

    dbUrl = connectionString + dbFilePath

    logger.info("Db file path: " + dbFilePath)
    logger.info("Database url: " + dbUrl)

    try {
      FileUtil.createFile(dbFilePath)

      connection = Option(DriverManager.getConnection(dbUrl))
      logger.info("Connection successful!")

      deleteTable()
      createTable()

      connection
    } catch {
      case exception: Exception => {
        logger.info("Exception while connecting to sqlite: " + exception.getMessage)
        throw exception
      }
    }
  }

  def deleteTable(): Unit = {
    var sql = s"DROP TABLE IF EXISTS logs"
    try {
      getConnection() match {
        case Some(conn) => {
          conn.createStatement().execute(sql)
          logger.info("Table deleted!")
        }
      }
    } catch {
      case exception: Exception => {
        logger.error("Error while deleting table: " + exception.getMessage)
        throw exception
      }
    }
  }

  def createTable(): Unit = {
    var sql = "CREATE TABLE IF NOT EXISTS logs ("
    sql += " id integer PRIMARY KEY AUTOINCREMENT, "
    sql += " date integer NOT NULL, "
    sql += " message text NOT NULL "
    sql += " ); "

    try {
      getConnection() match {
        case Some(conn) => {
          conn.createStatement().execute(sql)
          logger.info("Table created!")
        }
      }
    } catch {
      case exception: Exception => {
        logger.error("Error while creating table: " + exception.getMessage)
        throw exception
      }
    }
  }

  def insertLogs(logs: List[LogData]): Unit = {
    val insertSql = "INSERT INTO logs (date, message) VALUES(?, ?)"
    try {
      getConnection() match {
        case Some(conn) => {
          conn.setAutoCommit(false)
          logs.foreach(logData => {
            val pstmt = conn.prepareStatement(insertSql)
            pstmt.setLong(1, logData.date)
            pstmt.setString(2, logData.message)
            pstmt.executeUpdate()
            logger.info("Inserted: " + logData.date + " " + logData.message)
          })
          conn.commit()
          logger.info("Logs inserted successfully!")
        }
      }
    } catch {
      case exception: Exception => {
        logger.error("Error while inserting: " + exception.getMessage)
        throw exception
      }
    }
  }

  def getLogs(logRequest: LogRequest): Seq[LogData] = {
    DateUtil.handleDateFormat(logRequest.dateTimeFrom)
    DateUtil.handleDateFormat(logRequest.dateTimeUntil)

    val dateTimeFrom: Long = DateUtil.getEpoch(logRequest.dateTimeFrom)
    val dateTimeUntil: Long = DateUtil.getEpoch(logRequest.dateTimeUntil)
    val phrase: String = logRequest.phrase

    val sql = s"SELECT $dateCol, $messageCol FROM $tableName WHERE $dateCol BETWEEN " +
      s"$dateTimeFrom AND $dateTimeUntil AND instr($messageCol, '$phrase') > 0"
    try {
      getConnection() match {
        case Some(conn) => {
          var logData = Seq[LogData]()

          val stmt = conn.createStatement()
          val resultSet = stmt.executeQuery(sql)

          while (resultSet.next()) {
            val date = resultSet.getLong(dateCol)
            val message = resultSet.getString(messageCol)

            logData = logData :+ LogData(date, message)

            logger.info(s"Database query date: $date, message: $message")
          }

          logData
        }
      }
    } catch {
      case exception: Exception => {
        logger.error("Error while fetching database: " + exception.getMessage)
        Seq[LogData]()
      }
    }
  }

  def getLogDataResponse(logRequest: LogRequest): GetLogDataResponse = {
    val logdata = getLogs(logRequest)
    var highlightTextResponseSeq = Seq[HighlightTextResponse]()
    logdata.foreach(data => {
      val date = DateUtil.getDate(data.date)
      highlightTextResponseSeq = highlightTextResponseSeq :+ getHighlightTextResponse(date, data.message, logRequest.phrase)
    })
    GetLogDataResponse(highlightTextResponseSeq, logRequest.dateTimeFrom, logRequest.dateTimeUntil, logRequest.phrase)
  }

  def getHighlightTextResponse(dateTime: String, message: String, phrase: String): HighlightTextResponse = {
    var highlightTextSeq = Seq[HighlightText]()
    var msg = message
    var prefixLength = 0

    while (msg.contains(phrase)) {
      val index = msg.indexOfSlice(phrase)
      highlightTextSeq = highlightTextSeq :+ HighlightText(index + prefixLength, index + prefixLength + phrase.length - 1)
      prefixLength += index + phrase.length
      msg = msg.slice(index + phrase.length, msg.length)
    }

    HighlightTextResponse(dateTime, message, highlightTextSeq)
  }

  def getHistogram(logRequest: LogRequest): GetHistogramResponse = {
    val logData = getLogs(logRequest)

    var histogramSeq = Seq[Histogram]()
    logData.foreach(data => {
      val dateTime = DateUtil.getDate(data.date)
      val counts = data.message.sliding(logRequest.phrase.length).count(window => window == logRequest.phrase)
      histogramSeq = histogramSeq :+ Histogram(dateTime, counts)
    })

    GetHistogramResponse(histogramSeq, logRequest.dateTimeFrom, logRequest.dateTimeUntil, logRequest.phrase)
  }
}
