package models

import java.util.Date
import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import anorm.SqlParser._
import anorm._
import play.api.db.DBApi
import workers.Master


@javax.inject.Singleton
class OnStartup @Inject() (
  configuration: play.api.Configuration,
  fundsService: FundsService,
  actorSystem: ActorSystem) {

  // fetch data for the first time
  if(fundsService.findBlocking("B").isEmpty) {
    actorSystem.actorOf(Props(new Master(configuration, fundsService))) ! Master.Tick
  }

}
