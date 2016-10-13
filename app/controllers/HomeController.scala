package controllers

import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n._
import play.api.mvc._
import workers.Master
import workers.Master.Tick

class HomeController @Inject() (
  actorSystem: ActorSystem,
  configuration: play.api.Configuration,
  fundsService: FundsService,
  val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  val master = actorSystem.actorOf(Props(new Master(configuration, fundsService)), "master")

  def index = Action { Ok("Start searching... e.g. /list?filter=Titluproiuect:hotel Criss") }

  def list(filter: String) = Action.async { implicit request =>
    fundsService.find(filter).map(result => Ok(result.toString))
  }

  def refreshData() = Action.async { request =>
    fundsService.deleteAll().map { _ =>
      master ! Tick
      Ok("Refreshing ... It may take a while")
    }
  }

}
            
