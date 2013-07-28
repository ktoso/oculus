import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import scala.Some

object BuildSettings {
  val buildOrganization = "project13"
  val buildVersion      = "1.0.0"
  val buildScalaVersion = "2.10.2"

  val myAssemblySettings = assemblySettings ++
    Seq(
      test in assembly := {},
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
          case PathList("org", "apache", rest) => MergeStrategy.last
          case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.last
          case x => MergeStrategy.last
        }
      }
    )

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    resolvers   ++= Resolvers.myResolvers,
    parallelExecution in Test := false
  ) ++ myAssemblySettings

}

object Resolvers {

  val myResolvers = Seq(
    "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
    "EasyTesting Releases" at "http://repo1.maven.org/maven2/org/easytesting",
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    "Cascading / conjars" at "http://conjars.org/repo",
    "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos"
  )
}

object Versions {
  val guava = "14.0"
  val rainbow = "0.2"
        
  val mockito = "1.8.5"

  val akka = "2.1.4"

  val scalding = "0.8.6"

  val fest = "1.4"
  val lift = "2.5-M4"
}

object Dependencies {
  import Resolvers._
  import Versions._

  // scalding
  val scaldingCore = "com.twitter"     %% "scalding-core"    % Versions.scalding withSources()
  val scaldingDate = "com.twitter"     %% "scalding-date"    % Versions.scalding withSources()
  val scaldingArgs = "com.twitter"     %% "scalding-args"    % Versions.scalding withSources()

  val scaldingAll = Seq(scaldingCore, scaldingDate, scaldingArgs)

  // hadoop
//  val hadoopCore   = "org.apache.hadoop" % "hadoop-core"   % "2.0.0-mr1-cdh4.3.0"
//  val hadoopClient = "org.apache.hadoop" % "hadoop-client" % "2.0.0-mr1-cdh4.3.0"
  val hadoopCore   = "org.apache.hadoop" % "hadoop-core"   % "1.1.2"
  val hadoopClient = "org.apache.hadoop" % "hadoop-client" % "1.1.2"

  // hbase
  val hbase      = "org.apache.hbase"  % "hbase"       % "0.94.6-cdh4.3.1"


  val hadoops = Seq(hadoopCore, hadoopClient, hbase)

  // mongodb and related
  val liftJson              = "net.liftweb"            %% "lift-json"             % Versions.lift
  val casbah                = "org.mongodb"            %% "casbah-core"           % "2.5.0"
  val mongo                 = "org.mongodb"             % "mongo-java-driver"     % "2.9.2"
  val liftMongoRecord       = "net.liftweb"            %% "lift-mongodb-record"   % Versions.lift withSources()
  val rogue                 = "com.foursquare"         %% "rogue-lift"            % "2.0.0-beta22" intransitive() withSources()

  val rogueAll = Seq(liftJson, casbah, mongo, rogue, liftMongoRecord)

  // Logging
  val scalaLogging          = "com.typesafe"         %% "scalalogging-slf4j"      % "1.0.1"
  val logback               = "ch.qos.logback"        % "logback-classic"         % "1.0.0"
  val log4jOverSlf4j        = "org.slf4j"             % "log4j-over-slf4j"        % "1.6.1"
  val jclOverSlf4j          = "org.slf4j"             % "jcl-over-slf4j"          % "1.6.1"
  val julToSlf4jBridge      = "org.slf4j"             % "jul-to-slf4j"            % "1.6.1"

  val logging               = Seq(scalaLogging, logback, log4jOverSlf4j, jclOverSlf4j)

  // mysql and related
  val mysqlConnector        = "mysql"                  %  "mysql-connector-java"  % "5.1.15"
  val liftUtil              = "net.liftweb"            %% "lift-util"             % Versions.lift withSources()
  val liftSquerylRecord     = "net.liftweb"            %% "lift-squeryl-record"   % Versions.lift withSources()
  val c3p0                  = "c3p0"                   %  "c3p0"                  % "0.9.1.2"

  // general tools
  val scalaToolsTime        = "org.scala-tools.time"  %%  "time"                  % "0.5" intransitive()
  val jodaTime              = "joda-time"             %   "joda-time"             % "2.1"
  val jodaTimeConvert       = "org.joda"              %   "joda-convert"          % "1.2"
  val scalaz                = "org.scalaz"            %%  "scalaz-core"           % "6.0.4"
  val guava                 = "com.google.guava"      %   "guava"                 % Versions.guava

  // testing
  val scalaTest               = "org.scalatest"       % "scalatest_2.10" % "1.9.1"
  val mockito                 = "org.mockito"         % "mockito-core"   % Versions.mockito

  val testing                = Seq(scalaTest, mockito).map(_ % "test")

  val jsoup                  = "org.jsoup"         % "jsoup"               % "1.7.2"

  // akka2
  val akka2Actor             = "com.typesafe.akka" %% "akka-actor"          % Versions.akka
  val akka2Slf4j             = "com.typesafe.akka" %% "akka-slf4j"          % Versions.akka
  val akka2TestKit           = "com.typesafe.akka" %% "akka-testkit"        % Versions.akka % "test"
  val akka2Full              = Seq(akka2Actor, akka2Slf4j, akka2TestKit)

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
    settings = buildSettings ++ assemblySettings ++
      Seq(
        libraryDependencies ++= Seq(logback, hbase, scalaz, guava, rainbow) ++ logging ++ testing
      )
  ) 

  lazy val scalding = Project(
    "scalding",
    file("scalding"),
    settings = buildSettings ++ assemblySettings ++
      Seq(
        libraryDependencies ++= scaldingAll ++ hadoops ++ akka2Full ++ testing,
        mainClass in assembly := Some("pl.project13.scala.oculus.ScaldingJobRunner")
      )
  ) dependsOn(common)

  lazy val downloader = Project(
    "downloader",
    file("downloader"),
    settings = buildSettings ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(jsoup) ++ akka2Full ++ hadoops ++ testing,
      mainClass in assembly := Some("pl.project13.scala.oculus.DownloaderRunner")
    ) ++ Seq(
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
        {
          case x if x.toString.toLowerCase contains "manifest.mf" => MergeStrategy.discard
          case x => MergeStrategy.first
        }
      }
    )
  ) dependsOn(common)
}
