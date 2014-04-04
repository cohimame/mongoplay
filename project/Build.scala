import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  import BuildSettings._
  import Dependencies._

  val appName     = "mongoplay"
  val appVersion  = "0.7"

  val appDependencies = Seq(jdbc, anorm, reactiveMongo, play2ReactiveMongo)


  val main = play.Project(appName, appVersion, appDependencies).settings()


  object BuildSettings {
    val scalaVer = "2.10.2"
  }

  object Dependencies {
    lazy val akkaVersion = "2.2.0"
    lazy val reactiveMongo      =   "org.reactivemongo" %% "reactivemongo"       % "0.10.0"
    lazy val play2ReactiveMongo =   "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"

  }

}