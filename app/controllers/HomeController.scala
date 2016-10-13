package controllers

import javax.inject.Inject

import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n._
import play.api.mvc._

class HomeController @Inject() (
  fundsService: FundsService,
  val messagesApi: MessagesApi)
  extends Controller with I18nSupport {


  def index = Action { Ok("Start searching... e.g. /list?filter=Bucuresti") }

  def list(filter: String) = Action.async { implicit request =>
    fundsService.find(filter).map(result => Ok(result.toString))
  }


}
            
