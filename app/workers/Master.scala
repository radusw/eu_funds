package workers

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import models.{Funds, FundsService}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Element
import play.api.Logger
import workers.FundsProcessor.Read

import scala.collection.mutable

class Master(
  configuration: play.api.Configuration,
  fundsService: FundsService) extends Actor {

  import workers.Master._

  var router = {
    val routees = Vector.fill(8) {
      val r = context.actorOf(Props(new FundsProcessor(fundsService)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }


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
      for (url <- fileUrls) self ! FundsProcessor.Read(url)

    case work@FundsProcessor.Read(url) =>
      router.route(work, sender())

    case FundsProcessor.Done(linesPart) =>
      if(linesPart != 0)
        Logger.info(s"${context.self.path.name} Done importing links' data. $linesPart lines imported.")
      else
        Logger.info(s"${context.self.path.name} No data to import.")

    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props(new FundsProcessor(fundsService)))
      context watch r
      router = router.addRoutee(r)
  }
}

object Master {
  case object Tick
  case class Write(data: mutable.Buffer[Funds])
}
