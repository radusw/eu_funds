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
  fundService: FundService,
  actorSystem: ActorSystem) {

  actorSystem.actorOf(Props(new Master(configuration, fundService))) ! Master.Tick

}
