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
  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
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
  // Play JSON
  "com.typesafe.play"       %% "play-json" % "2.9.2",
  "com.typesafe.play"       %% "play"      % "2.8.16",
  "org.playframework.anorm" %% "anorm"     % "2.6.10",
  // Keycloak
  "org.keycloak" % "keycloak-core"         % "18.0.0",
  "org.keycloak" % "keycloak-adapter-core" % "18.0.0",
  // Auth
  "com.github.jwt-scala" %% "jwt-circe" % "9.0.2",
)
