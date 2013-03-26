import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "project13"
  val buildVersion      = "1.0.0"
  val buildScalaVersion = "2.9.2"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    resolvers   ++= Resolvers.myResolvers
  )
}

object Resolvers {

  val myResolvers = Seq(
    "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
    "EasyTesting Releases" at "http://repo1.maven.org/maven2/org/easytesting",
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  )
}

object Versions {
  val guava = "14.0"
  val rainbow = "0.1"
        
  val mockito = "1.8.5"
  val scalatest = "2.0.M5-B1"
        
  val fest = "1.4"
  val lift = "2.5-M4"
}

object Dependencies {
  import Resolvers._
  import Versions._

  // scalding
  val scaldingLib = "com.twitter" % "scalding_2.9.2" % "0.8.3" withSources()

  // mongodb and related
  val liftJson              = "net.liftweb"            %% "lift-json"             % Versions.lift
  val casbah                = "org.mongodb"            %% "casbah-core"           % "2.5.0"
  val mongo                 = "org.mongodb"            %  "mongo-java-driver"     % "2.9.2"
  val liftMongoRecord       = "net.liftweb"            %% "lift-mongodb-record"   % Versions.lift withSources()
  val rogue                 = "com.foursquare"         %% "rogue-lift"            % "2.0.0-beta22" intransitive() withSources()

  val rogueAll = Seq(liftJson, casbah, mongo, rogue, liftMongoRecord)

  // Logging
  val slf4s                 = "com.weiglewilczek.slf4s" %% "slf4s"                % "1.0.7"
  val logback               = "ch.qos.logback"        % "logback-classic"         % "1.0.0"
  val log4jOverSlf4j        = "org.slf4j"             % "log4j-over-slf4j"        % "1.6.1"
  val jclOverSlf4j          = "org.slf4j"             % "jcl-over-slf4j"          % "1.6.1"
  val julToSlf4jBridge      = "org.slf4j"             % "jul-to-slf4j"            % "1.6.1"

  val logging               = Seq(slf4s, logback, log4jOverSlf4j, jclOverSlf4j)

  // mysql and related
  val mysqlConnector        = "mysql"                  %  "mysql-connector-java"  % "5.1.15"
  val liftUtil              = "net.liftweb"            %% "lift-util"             % Versions.lift withSources()
  val liftSquerylRecord     = "net.liftweb"            %% "lift-squeryl-record"   % Versions.lift withSources()
  val c3p0                  = "c3p0"                   %  "c3p0"                  % "0.9.1.2"

  // general tools
  val scalaToolsTime        = "org.scala-tools.time"  %%  "time"                  % "0.5" intransitive()
  val jodaTime              = "joda-time"             %   "joda-time"             % "2.1"
  val jodaTimeConvert       = "org.joda"              %   "joda-convert"          % "1.2"
  val scalaz                = "org.scalaz"            %% "scalaz-core"            % "6.0.4"
  val guava                 = "com.google.guava"    % "guava"                   % Versions.guava

  // testing
  val scalaTest               = "org.scalatest"       % "scalatest_2.10.0-RC3"    % Versions.scalatest
  val scalaTest_2_9           = "org.scalatest"          %% "scalatest"             % "1.8"
  val mockito                 = "org.mockito"         % "mockito-core"            % Versions.mockito

  val testing_2_10            = Seq(scalaTest, mockito).map(_ % "test")
  val testing_2_9             = Seq(scalaTest_2_9, mockito).map(_ % "test")


  val jsoup                  = "org.jsoup"         % "jsoup"               % "1.7.2"

  // akka2
  val akka2Version           = "2.0.5"
  val akka2Actor             = "com.typesafe.akka" % "akka-actor"          % akka2Version
  val akka2ZeroMQ            = "com.typesafe.akka" % "akka-zeromq"         % akka2Version
  val akka2Slf4j             = "com.typesafe.akka" % "akka-slf4j"          % akka2Version
  val akka2TestKit           = "com.typesafe.akka" % "akka-testkit"        % akka2Version % "test"
  val akka2Full              = Seq(akka2Actor, akka2ZeroMQ, akka2Slf4j, akka2TestKit)

  // terminal coloring
  val rainbow                 = "pl.project13.scala"      %% "rainbow"                   % Versions.rainbow

  // java stuff
  val festAssert              = "org.easytesting"          % "fest-assert"               % Versions.fest      % "test"

}

object OculusBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root = Project(
    "root",
    file("."),
    settings = buildSettings
  ) aggregate(common, scalding, downloader)


  lazy val common = Project(
    "common",
    file("common"),
    settings = buildSettings ++
      Seq(
        libraryDependencies ++= Seq(logback, scalaz, guava) ++ testing_2_9
      )
  ) 

  lazy val scalding = Project(
    "scalding",
    file("scalding"),
    settings = buildSettings ++
      Seq(libraryDependencies ++= Seq(scaldingLib) ++ akka2Full ++ testing_2_9)
  ) dependsOn(common)

  lazy val downloader = Project(
    "downloader",
    file("downloader"),
    settings = buildSettings ++
      Seq(libraryDependencies ++= Seq(scaldingLib, jsoup) ++ akka2Full ++ testing_2_9)
  ) dependsOn(common)
}
