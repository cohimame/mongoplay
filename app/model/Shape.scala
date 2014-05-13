package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Point(x: Float, y: Float)
object Point {
  implicit val pointReads = Json.reads[Point]
  implicit val pointWrites = Json.writes[Point]
}


abstract sealed trait Shape
case class Circle(origin: Point, radius: Float) extends Shape
case class Polygon(points: List[Point]) extends Shape

object Shape {
  implicit val shapeReads = {
    val cr = Json.reads[Circle]
    val pr = Json.reads[Polygon]

    __.read[Circle](cr).map(x => x: Shape) |
      __.read[Polygon](pr).map(x => x: Shape)
  }

  implicit val shapeWrites = Writes[Shape] {
    shape => shape match {
      case circle: Circle => Json.writes[Circle].writes(circle)
      case poly: Polygon =>  Json.writes[Polygon].writes(poly)
    }
  }

}




