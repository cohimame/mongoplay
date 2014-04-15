package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.iteratee._

import scala.concurrent.{ ExecutionContext, Future }
import reactivemongo.api._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection

import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Streamer extends Controller with MongoController {

  val stream: Future[JSONCollection] = {
    def collection: JSONCollection = db.collection[JSONCollection]("users")

    collection.stats().flatMap {
      case stats if !stats.capped =>
        println("converting to capped")
        collection.convertToCapped(1024 * 1024, None)
      case _ => Future(collection)
    }.recover {
      // the collection does not exist, so we create it
      case _ =>
        println("creating capped collection...")
        collection.createCapped(1024 * 1024, None)
    }.map { _ =>
      println("the capped collection is available")
      collection
    }

  }

  def socket = WebSocket.using[JsValue] { request =>
  // Inserts the received messages into the capped collection
    val in = Iteratee.flatten(
      stream.map(collection =>
        Iteratee.foreach[JsValue] { json =>
          println("received " + json)
          collection.insert(json)
        }
      )
    )

    // Enumerates the capped collection
    val out = {
      val futureEnumerator = stream.map { collection =>
      // so we are sure that the collection exists and is a capped one
        val cursor: Cursor[JsValue] = collection
          // we want all the documents
          .find(Json.obj())
          // the cursor must be tailable and await data
          .options(QueryOpts().tailable.awaitData)
          .cursor[JsValue]

        // ok, let's enumerate it
        cursor.enumerate()
      }
      Enumerator.flatten(futureEnumerator)
    }

    // We're done!
    (in, out)

  }

}
