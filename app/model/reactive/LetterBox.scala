package model.reactive

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.LastError

import play.api.Play.current

import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class Letter(text: String)
object Letter {
  import play.api.libs.json.Json
  implicit val reads = Json.reads[Letter]
  implicit val writes = Json.writes[Letter]
}

object LetterBox {
  /*
      база данных, указанная в application.conf
      (MongoController использует её же)
   */
  def db = ReactiveMongoPlugin.db

  def letterColl = db.collection[JSONCollection]("letterbox")

  def put(letter: Letter): Future[LastError] = letterColl.insert(letter)

  def find(query: String) = {
    val jsonQuery = Json.obj("$regex" -> query)
    letterColl.find(jsonQuery).cursor[Letter].collect[List]()
  }

}