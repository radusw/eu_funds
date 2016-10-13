package models

import javax.inject.Inject

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.settings.Settings
import play.api.Logger

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class FundsService @Inject() (
  configuration: play.api.Configuration
) {

  val settings = Settings.settingsBuilder()
    .put("http.enabled", false)
    .put("path.home", configuration.underlying.getString("elastic.data.path"))
  val client = ElasticClient.local(settings.build)

  Try(insertBlocking(Funds(mutable.Buffer.empty))) match {
    case Success(_) => ()

    case Failure(cause) =>
      Logger.error(s"Could not create index - " + cause)
  }

  def find(filter: String) = {
    client.execute { search in "gov" / "funds" query filter limit 10 }
  }

  def findBlocking(filter: String) = {
    client.execute { search in "gov" / "funds" query filter limit 10 }.await
  }

  def insert(funds: Funds) = {
    client.execute { index into "gov" / "funds" fields funds.data }
  }

  def insertBlocking(funds: Funds) = {
    client.execute { index into "gov" / "funds" fields funds.data }.await
  }

  def deleteAll() = {
    client.execute { deleteIndex("gov") }
  }
}

case class Funds(data: mutable.Buffer[(String, String)])
