package com.loganalyzer

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.sql.{Connection, DriverManager}

object LogRepository {
  var connection: Option[Connection] = None
  val logger = LoggerFactory.getLogger("LogRepository")
  var dbUrl: String = ""

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

    logger.info("showing config file" + dbUrl)

    try {
      connection = Option(DriverManager.getConnection(dbUrl))
      logger.info("Connection successful")
      createTable()
      connection
    } catch {
      case e: Exception => {
        logger.info("Got exception while connecting to sqlite: " + e.getMessage)
        None
      }
    } finally {
//      try {
//        connection match {
//          case Some(conn) => conn.close()
//        }
//        println("connection closed!")
//      } catch {
//        case e: Exception => println("Exception while closing connection")
//      }
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
          logger.info("Table created")
        }
      }
    } catch {
      case ex: Exception => logger.error("Error while creating table: " + ex.getMessage)
    }
  }
}
