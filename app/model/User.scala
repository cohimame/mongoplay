package model

import play.api.libs.json.Json

case class User(login: String, email: String)

object User {

    //Json.format[T] is a macro, that generates Reads & Writes for a case class


    implicit val adminReads = Json.reads[User]
    implicit val adminWrites = Json.writes[User]
  /*
  implicit val adminFormat = Json.format[User]
  */

}
