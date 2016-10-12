package workers

import akka.actor.{Actor, ActorRef, Props}
import models.{Fund, FundService}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Element
import play.api.Logger
import scala.collection.mutable

class Master(
  configuration: play.api.Configuration,
  fundService: FundService) extends Actor {

  import workers.Master._

  override def receive = ready

  override def unhandled(message: Any): Unit = {
    Logger.warn(context.self.path.name + " - " + message.toString)
  }

  def ready: Receive = {

    case Tick =>
      val browser = JsoupBrowser()
      val doc = browser.get(configuration.underlying.getString("gov.url"))
      val items: List[Element] = doc >> elementList("a.resource-url-analytics")
      val fileUrls: Set[String] = items.map(_.attr("href")).toSet

      Logger.info(s"${context.self.path.name} Reading data from " + fileUrls.size + " links ...")
      val readers = for {
        (url, idx) <- fileUrls.zipWithIndex
      } yield {
        context.actorOf(Props(new FundsProcessor(url)), s"$idx-Reader-${url.replaceAll("\\W", "").takeRight(32)}")
      }

      readers foreach (_ ! FundsProcessor.Read)
      context become reading(readers, 0)
  }

  def reading(readers: Set[ActorRef], linesNo: Int): Receive = {

    case FundsProcessor.Done(linesPart) =>
      val newReaders = readers - sender

      if(newReaders.isEmpty) {
        val allLines = linesNo + linesPart
        println(allLines)
        if(allLines != 0) {
          Logger.info(s"${context.self.path.name} Done reading links' data. Start writing data ...")
        }
        else {
          Logger.info(s"${context.self.path.name} No data to write.")
          context become ready
        }
      }
      else {
        context become reading(newReaders, linesNo + linesPart)
      }

    case Tick =>
      Logger.error(s"${context.self.path.name} reading :: Tick ignored.")
  }


}

object Master {
  case object Tick
  case class Write(data: mutable.Buffer[Fund])
}
