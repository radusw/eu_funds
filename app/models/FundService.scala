package models

import javax.inject.Inject

import play.api.db.DBApi

import scala.collection.mutable

case class Fund(data: mutable.Buffer[(String, String)])

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}


@javax.inject.Singleton
class FundService @Inject() (dbapi: DBApi) {

  private val db = dbapi.database("default")

}
