package controllers

import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n._
import play.api.mvc._
import play.twirl.api.Html
import views.html
import workers.Master
import workers.Master.Tick

class HomeController @Inject() (
  actorSystem: ActorSystem,
  configuration: play.api.Configuration,
  fundsService: FundsService,
  val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  val master = actorSystem.actorOf(Props(new Master(configuration, fundsService)), "master")

  def index = Action { Ok(html.main(Html("Start searching... e.g. /list?filter=Pitești&from=0&size=10"))) }

  def list(filter: String, from: Int, size: Int) = Action.async { implicit request =>
    fundsService.find(filter, from, size).map(result => Ok(html.main(Html(result.toString))))
  }

  def refreshData() = Action.async { request =>
    fundsService.deleteAll().map { _ =>
      master ! Tick
      Ok(html.main(Html("Refreshing ... It may take a while")))
    }
  }

}
            
