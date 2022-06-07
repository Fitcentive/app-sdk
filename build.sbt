name := "app-sdk"
organization := "io.fitcentive"

scalaVersion := "2.13.8"
version := Option(System.getProperty("version")).getOrElse("1.0.0")

packageOptions += Package.ManifestAttributes(
  "Built-By" -> System.getProperty("user.name"),
  "Build-Jdk" -> System.getProperty("java.version"),
)

startYear := Some(2022)
javacOptions ++= Seq("-source", "1.11", "-target", "1.11", "-Xlint:unchecked", "-encoding", "UTF-8")
scalacOptions ++= Seq("-Ymacro-annotations", "-unchecked", "-deprecation", "-language:_", "-encoding", "UTF-8")
cancelable in Global := true
Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies ++= Seq(
  //Config
  "com.typesafe" % "config" % "1.4.1",
  //Logging
  "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.4",
  "ch.qos.logback"              % "logback-classic"          % "1.3.0-alpha10",
  "net.logstash.logback"        % "logstash-logback-encoder" % "7.0.1",
  //Google PubSub
  "com.google.cloud" % "google-cloud-pubsub"  % "1.115.1",
  "com.google.cloud" % "google-cloud-storage" % "2.2.3",
  //Cats
  "org.typelevel" %% "cats-core"   % "2.7.0",
  "org.typelevel" %% "cats-effect" % "3.3.4",
  //Circe
  "io.circe" %% "circe-core"           % "0.14.1",
  "io.circe" %% "circe-generic"        % "0.14.1",
  "io.circe" %% "circe-generic-extras" % "0.14.1",
  "io.circe" %% "circe-parser"         % "0.14.1",
)

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core"        % "2.11.4",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.11.4",
  "com.fasterxml.jackson.core" % "jackson-databind"    % "2.11.4",
)
