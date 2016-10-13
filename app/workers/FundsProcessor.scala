package workers

import java.io.InputStream

import akka.actor.Actor

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import akka.actor._
import models.{Funds, FundsService}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{CellType, Row}
import play.api.Logger

import scala.collection.mutable
import scala.concurrent._
import scala.language.postfixOps
import scala.util.control.NonFatal

class FundsProcessor(url: String, fundsService: FundsService) extends Actor {

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
      val lines: mutable.Buffer[Funds] =
        Try {
          parseXls(stream)
        } match {
          case Success(result) =>
            result

          case Failure(cause) =>
            Logger.warn(s"$url - Could not parse body - " + cause)
            mutable.Buffer.empty
        }
      if (lines.nonEmpty) {
        Logger.info(s"${context.self.path.name}  - Starting writing ${lines.size} lines")
        lines.foreach(line => fundsService.insertBlocking(line))
        Logger.warn(s"${context.self.path.name}  - Done inserting ${lines.size}.")
      }
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
    val funds = mutable.Buffer.empty[Funds]

    val sheets = new HSSFWorkbook(stream).sheetIterator()
    while (sheets.hasNext) {
      val sheet = sheets.next()
      val rows = sheet.rowIterator()

      val firstRow = rows.next()
      val title = firstRow.cellIterator().toBuffer[org.apache.poi.ss.usermodel.Cell].map { cell =>
        cell.getCellTypeEnum match {
          case CellType.STRING => cell.getStringCellValue.replaceAll("\\W", "")
          case CellType.NUMERIC => cell.getNumericCellValue.toString.replaceAll("\\W", "")
          case _ => "N/A"
        }
      }

      while(rows.hasNext) try {
        funds += Funds(
          rows.next().cellIterator().toBuffer[org.apache.poi.ss.usermodel.Cell].zipWithIndex.map { case (cell, idx) =>
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
      } catch {
        case NonFatal(e) => Logger.warn(s"$url - Could not parse row - " + e)
      }
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
