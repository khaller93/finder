name := """Web application (finder)"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

resolvers += "Local Maven repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository"

libraryDependencies += "at.ac.tuwien.finder" % "service" % "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  javaWs
)
