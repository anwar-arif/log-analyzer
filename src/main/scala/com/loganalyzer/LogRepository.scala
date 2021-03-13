package com.loganalyzer

import com.loganalyzer.Models.DataModel._
import com.loganalyzer.utils.DateUtil
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
    val dbFilepath = config.getString("app.sqlite.filePath")

    dbUrl = connectionString + dbFilepath

    logger.info("Database url: " + dbUrl)

    try {
      connection = Option(DriverManager.getConnection(dbUrl))
      logger.info("Connection successful!")
      deleteTable()
      createTable()
      connection
    } catch {
      case e: Exception => {
        logger.info("Exception while connecting to sqlite: " + e.getMessage)
        None
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
      case ex: Exception => logger.error("Error while deleting table: " + ex.getMessage)
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
      case ex: Exception => logger.error("Error while creating table: " + ex.getMessage)
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
      case ex: Exception => logger.error("Error while inserting: " + ex.getMessage)
    }
  }

  def getLogs(logRequest: LogRequest): GetLogDataResponse = {
    val dateTimeFrom: Long = DateUtil.getEpoch(logRequest.dateTimeFrom)
    val dateTimeUntil: Long = DateUtil.getEpoch(logRequest.dateTimeUntil)
    val phrase: String = logRequest.phrase

    val sql = s"SELECT $dateCol, $messageCol FROM $tableName WHERE $dateCol BETWEEN " +
      s"$dateTimeFrom AND $dateTimeUntil AND instr($messageCol, '$phrase') > 0"
    try {
      getConnection() match {
        case Some(conn) => {
          var resultData = Seq[LogData]()

          val stmt = conn.createStatement()
          val resultSet = stmt.executeQuery(sql)
          while (resultSet.next()) {
            val date = resultSet.getLong(dateCol)
            val message = resultSet.getString(messageCol)

            resultData = resultData :+ LogData(date, message)

            logger.info(s"Database query date: $date, message: $message")
          }

          GetLogDataResponse(resultData)
        }
      }
    } catch {
      case ex: Exception => {
        logger.error("Error while fetching database: " + ex.getMessage)
        GetLogDataResponse(Seq.empty)
      }
    }
  }
}
