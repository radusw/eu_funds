name := "eu-funds"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.0"
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.1.0"
libraryDependencies += "org.apache.poi" % "poi" % "3.15"
libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s-core_2.11" % "2.3.1"
libraryDependencies += "net.java.dev.jna" % "jna" % "3.3.0"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test

