package workers

import java.io.InputStream

import akka.actor.Actor

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import akka.actor._
import models.Fund
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{CellType, Row}
import play.api.Logger
import workers.Master.Write

import scala.collection.mutable
import scala.concurrent._
import scala.language.postfixOps

class FundsProcessor(url: String) extends Actor {

  import FundsProcessor._

  implicit val exec = context.dispatcher

  override def receive = reading(retries)

  def reading(retries: Int): Receive = {
    case Read =>
      if (retries > 0) {
        Logger.info(s"${context.self.path.name} Reading from $url :: retry[${retries - retries}]")
        self ! blocking { Try(get(url, timeout/2, timeout, "GET")) }
      }
      else {
        Logger.error(s"${context.self.path.name} Reading was not successful. No more retries.")
        stop()
      }

    case Retry =>
      context become reading(retries - 1)
      val retryNo = retries - retries + 1
      context.system.scheduler.scheduleOnce((1 << retryNo) seconds, self, Read) // exponential backoff

    case Success(stream: InputStream) =>
      val lines: mutable.Buffer[Fund] =
        Try {
          parseXls(stream)
        } match {
          case Success(result) => result

          case Failure(cause) =>
            Logger.warn(s"${context.self.path.name} Could not parse body - " + cause)
            mutable.Buffer.empty
        }
      lines.take(10).foreach(fund => println(fund)) //TODO insert
      stop(lines.size)

    case Failure(cause) =>
      Logger.warn(s"${context.self.path.name} Reading failed - " + cause)
      self ! Retry
  }

  def stop(size: Int = 0): Unit = {
    context.parent ! FundsProcessor.Done(size)
    context.stop(self)
  }

  @throws(classOf[java.io.IOException])
  @throws(classOf[java.net.SocketTimeoutException])
  def get(
    url: String,
    connectTimeout:Int = 5000,
    readTimeout:Int = 10000,
    requestMethod: String = "GET") = {

    import java.net.{URL, HttpURLConnection}

    val connection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)

    connection.getInputStream
  }

  def parseXls(stream: InputStream) = {
    val funds = mutable.Buffer.empty[Fund]

    val sheets = new HSSFWorkbook(stream).sheetIterator()
    while (sheets.hasNext) {
      val sheet = sheets.next()
      val rows = sheet.rowIterator().toBuffer[Row]
      val title = rows.head.cellIterator().toBuffer[org.apache.poi.ss.usermodel.Cell].map { cell =>
        cell.getCellTypeEnum match {
          case CellType.STRING => cell.getStringCellValue
          case CellType.NUMERIC => cell.getNumericCellValue.toString
          case _ => "N/A"
        }
      }
      val entries = rows.tail.map { row =>
        Try {
          Fund(
            row.cellIterator().toBuffer[org.apache.poi.ss.usermodel.Cell].zipWithIndex.map { case (cell, idx) =>
              val value = cell.getCellTypeEnum match {
                case CellType.STRING => cell.getStringCellValue
                case CellType.NUMERIC => cell.getNumericCellValue.toString
                case CellType.BOOLEAN => cell.getBooleanCellValue.toString
                case CellType.FORMULA => cell.getCachedFormulaResultTypeEnum match {
                  case CellType.STRING => cell.getStringCellValue
                  case CellType.NUMERIC => cell.getNumericCellValue.toString
                  case _ => ""
                }
                case CellType.BLANK | CellType.ERROR | CellType._NONE => ""
              }
              title(idx) -> value
            })
        }.toOption
      }
      funds ++= entries.flatten
    }

    funds
  }
}

object FundsProcessor {
  val retries = 8
  val timeout = 12000

  case class Read(url: String)
  case class Done(noOfLinesWritten: Int)
  case object Retry
}