package models

import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import workers.Master
import workers.Master.Tick


@javax.inject.Singleton
class OnStartup @Inject() (
  configuration: play.api.Configuration,
  fundsService: FundsService,
  actorSystem: ActorSystem) {

  // fetch data for the first time
  if(fundsService.findBlocking("B").isEmpty) {
    val master = actorSystem.actorOf(Props(new Master(configuration, fundsService)))
    master ! Tick
  }

}
