/* the multi-backend build setup is largely copied from
 * https://github.com/jkutner/play-with-scalajs-example/blob/master/build.sbt
 * (thanks!)
 */

import sbt.Project.projectToRef

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
		  "org.webjars" % "jquery" % "1.11.1"),
		resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
		// Play provides two styles of routers, one expects its actions to be injected, the
		// other, legacy style, accesses its actions statically.
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
//		sourceMapsDirectories += sharedJs.base / "..",
//		unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
		libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
		libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"))
  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(sharedJs)
  
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(scalaVersion := scalaV)
  .jsConfigure(_ enablePlugins ScalaJSPlay)
//  .jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
