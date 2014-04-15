package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import reactivemongo.api.Cursor

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

import model.User
import model.User._
import play.api.libs.iteratee.Iteratee

object Database extends Controller with MongoController {
  def users: JSONCollection = db.collection[JSONCollection]("users")

  def acceptUser = Action.async(parse.json) { request =>
    val json = request.body
    val maybeValid = json.validate[User]

    Logger info s"got $json body which was validated as $maybeValid"


    maybeValid map { user =>
      users.insert(user) map { lastError => Created(s"Created with $lastError") }
    } getOrElse {
      Future.successful(BadRequest("invalid json"))
    }

  }

  def findUsers = Action.async {
    val cursor: Cursor[User] = users.find(Json.obj("login" -> Json.obj("$regex" -> ".*"))).cursor[User]

    val futureUsersList: Future[List[User]] = cursor.collect[List]()

    val futurePersonsJsonArray = futureUsersList map { users => Json.arr(users) }

    futurePersonsJsonArray map { users =>  Ok(users(0)) }
  }


}
