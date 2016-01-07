/* the multi-backend build setup is largely copied from
 * https://github.com/jkutner/play-with-scalajs-example/blob/master/build.sbt
 * (thanks!)
 */

import sbt.Project.projectToRef
import NativePackagerKeys._

lazy val clients = Seq(scalajs)
lazy val scalaV = "2.11.7"

lazy val commonSettings = Seq(
  name := """sojoe-dot-at""",
  version := "1.0-SNAPSHOT",
  scalaVersion := scalaV)

lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(Seq(libraryDependencies ++= Seq(
		  jdbc,
		  cache,
		  ws,
		  specs2 % Test,
		  "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
		  "org.webjars" % "jquery" % "1.11.1",
		  "org.pegdown" % "pegdown" % "1.6.0"),
		resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
		routesGenerator := InjectedRoutesGenerator,
		scalaJSProjects := clients,
		pipelineStages := Seq(scalaJSProd, gzip)
	      ))
  .enablePlugins(PlayScala)
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(sharedJvm)

lazy val scalajs = (project in file("scalajs"))
  .settings(commonSettings)
  .settings(Seq(persistLauncher := true,
		persistLauncher in Test := false,
		libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
		libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"))
  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(sharedJs)
  .dependsOn(twodee)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(scalaVersion := scalaV)
  .jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val twodee = RootProject(file("twodee"))

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
