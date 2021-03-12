package com.loganalyzer

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.sql.{Connection, DriverManager}

case class DbLogData(date: Long, message: String)

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
    dbUrl = config.getString("app.sqlite.connectionString")

    logger.info("Database url: " + dbUrl)

    try {
      connection = Option(DriverManager.getConnection(dbUrl))
      logger.info("Connection successful!")
      createTable()
      connection
    } catch {
      case e: Exception => {
        logger.info("Exception while connecting to sqlite: " + e.getMessage)
        None
      }
    }
  }

  def createTable(): Unit = {
    var sql = "CREATE TABLE IF NOT EXISTS logs ("
    sql += " date integer NOT NULL, "
    sql += " message text NOT NULL, "
    sql += " PRIMARY KEY (date, message) "
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

  def insertLogs(logs: Seq[DbLogData]): Unit = {
    val insertSql = "INSERT INTO ? (?, ?) VALUES(?, ?)"
    try {
      getConnection() match {
        case Some(conn) => {
          conn.setAutoCommit(false)
          logs.foreach(logData => {
            val pstmt = conn.prepareStatement(insertSql)
            pstmt.setString(1, tableName)
            pstmt.setString(2, dateCol)
            pstmt.setString(3, messageCol)
            pstmt.setLong(4, logData.date)
            pstmt.setString(5, logData.message)
            pstmt.executeUpdate()
          })
          conn.commit()
          logger.info("Logs inserted successfully!")
        }
      }
    } catch {
      case ex: Exception => logger.error("Error while inserting: " + ex.getMessage)
    }
  }
}
