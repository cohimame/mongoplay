package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.Logger

object Database extends Controller with MongoController {
  def users: JSONCollection = db.collection[JSONCollection]("users")




  def acceptUser = Action.async(parse.json) { request =>
    val json = request.body

    users.insert(json) map {
      lastError => Created(s"Created with $lastError")
    }

  }

  def findUser = Action.async(parse.json) { request =>
    import model.User
    import model.User._

    val json = request.body

    val user = adminReads.reads(json)

    user.fold(
      invalid = {errors => },
      valid = { u =>  }
    )

    Logger info s"got $user"

    val future = users.find(user).one[User]

    future map {
     _ map { user => Ok("okay") } getOrElse { BadRequest(s"Not found $user in DB") }
    }

  }

}
