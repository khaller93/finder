name := """Web application (finder)"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

scalaVersion := "2.11.7"

resolvers += "Local Maven repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository"

includeFilter in (Assets, LessKeys.less) := "*.less"

LessKeys.compress := true

libraryDependencies += "org.webjars" % "bootstrap" % "3.3.7"
libraryDependencies += "at.ac.tuwien.finder" % "service" % "0.1-SNAPSHOT"
libraryDependencies += "at.ac.tuwien.finder" % "dto" % "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  javaWs
)
