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

}
