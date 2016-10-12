package controllers

import javax.inject.Inject

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import views._

class HomeController @Inject() (
  fundService: FundService,
  val messagesApi: MessagesApi)
  extends Controller with I18nSupport {


  def index = Action { Ok }

  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok("")
  }


}
            
