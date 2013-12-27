import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import scala.Some

object BuildSettings {
  val buildOrganization = "pl.project13"
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
    "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos",
    "SpyGlass" at "https://github.com/ParallelAI/mvn-repo/raw/master/releases",
    "Parallel AI" at "https://github.com/ParallelAI/mvn-repo/raw/master/releases"
  )
}

object Versions {
  val guava = "14.0"
  val rainbow = "0.2"
        
  val mockito = "1.8.5"

  val akka = "2.1.4"

  val scalding = "0.8.8"
  val cascading = "2.1.6"

  val fest = "1.4"
  val lift = "2.5-M4"

  val spyGlass = "2.10.2_2.3.0"
}

object Dependencies {
  import Resolvers._
  import Versions._

//  lazy val spyGlass     = "parallelai"       % "parallelai.spyglass" % "2.0.3"

  // scalding
  lazy val scaldingCore = "com.twitter"     %% "scalding-core"       % Versions.scalding withSources()
  lazy val scaldingDate = "com.twitter"     %% "scalding-date"       % Versions.scalding withSources()
  lazy val scaldingArgs = "com.twitter"     %% "scalding-args"       % Versions.scalding withSources()

//  lazy val cascadingLocal = "cascading"      % "cascading-local"     % Versions.cascading % "test" withSources()

  lazy val scaldingAll = Seq(scaldingCore, scaldingDate, scaldingArgs)

  lazy val powerMockAll =
    Seq(
      "org.powermock" % "powermock"               % "1.5.2",
      "org.powermock" % "powermock-api-mockito"   % "1.5.2",
      "org.powermock" % "powermock-module-junit4" % "1.5.2"
    )

  // hadoop
//  lazy val hadoopCore   = "org.apache.hadoop" % "hadoop-core"   % "2.0.0-mr1-cdh4.3.0"
//  lazy val hadoopClient = "org.apache.hadoop" % "hadoop-client" % "2.0.0-mr1-cdh4.3.0"
  lazy val hadoopCore   = "org.apache.hadoop" % "hadoop-core"   % "1.1.2"
  lazy val hadoopClient = "org.apache.hadoop" % "hadoop-client" % "1.1.2"

  // hbase
  lazy val hPaste     = "com.gravity"       % "gravity-hpaste"      % "0.1.11"
  lazy val hbase      = "org.apache.hbase"  % "hbase"               % "0.94.6.1"

  lazy val hadoops = Seq(hadoopCore, hadoopClient, hbase)

  // json
  lazy val json4sJackson         = "org.json4s"             %% "json4s-jackson"        % "3.2.5"
  lazy val liftJson              = "net.liftweb"            %% "lift-json"             % Versions.lift

  // mongodb and related
  lazy val casbah                = "org.mongodb"            %% "casbah-core"           % "2.5.0"
  lazy val mongo                 = "org.mongodb"             % "mongo-java-driver"     % "2.9.2"
  lazy val liftMongoRecord       = "net.liftweb"            %% "lift-mongodb-record"   % Versions.lift withSources()
  lazy val rogue                 = "com.foursquare"         %% "rogue-lift"            % "2.0.0-beta22" intransitive() withSources()

  lazy val rogueAll = Seq(liftJson, casbah, mongo, rogue, liftMongoRecord)

  // Logging
  lazy val scalaLogging          = "com.typesafe"         %% "scalalogging-slf4j"      % "1.0.1"
  lazy val logback               = "ch.qos.logback"        % "logback-classic"         % "1.0.0"
  lazy val log4jOverSlf4j        = "org.slf4j"             % "log4j-over-slf4j"        % "1.6.1"
  lazy val jclOverSlf4j          = "org.slf4j"             % "jcl-over-slf4j"          % "1.6.1"
  lazy val julToSlf4jBridge      = "org.slf4j"             % "jul-to-slf4j"            % "1.6.1"

  lazy val logging               = Seq(scalaLogging, logback, log4jOverSlf4j, jclOverSlf4j)

  // mysql and related
  lazy val mysqlConnector        = "mysql"                  %  "mysql-connector-java"  % "5.1.15"
  lazy val liftUtil              = "net.liftweb"            %% "lift-util"             % Versions.lift withSources()
  lazy val liftSquerylRecord     = "net.liftweb"            %% "lift-squeryl-record"   % Versions.lift withSources()
  lazy val c3p0                  = "c3p0"                   %  "c3p0"                  % "0.9.1.2"

  // general tools
  lazy val scalaToolsTime        = "org.scala-tools.time"  %%  "time"                  % "0.5" intransitive()
  lazy val jodaTime              = "joda-time"             %   "joda-time"             % "2.1"
  lazy val jodaTimeConvert       = "org.joda"              %   "joda-convert"          % "1.2"
  lazy val scalaz                = "org.scalaz"            %%  "scalaz-core"           % "6.0.4"
  lazy val guava                 = "com.google.guava"      %   "guava"                 % Versions.guava

  // testing
  lazy val scalaTest               = "org.scalatest"       % "scalatest_2.10" % "1.9.1"
  lazy val mockito                 = "org.mockito"         % "mockito-core"   % Versions.mockito

  lazy val testing                = Seq(scalaTest, mockito).map(_ % "test")

  lazy val jsoup                  = "org.jsoup"         % "jsoup"               % "1.7.2"

  // akka2
  lazy val akka2Actor             = "com.typesafe.akka" %% "akka-actor"          % Versions.akka
  lazy val akka2Slf4j             = "com.typesafe.akka" %% "akka-slf4j"          % Versions.akka
  lazy val akka2TestKit           = "com.typesafe.akka" %% "akka-testkit"        % Versions.akka % "test"
  lazy val akka2Full              = Seq(akka2Actor, akka2Slf4j, akka2TestKit)

  // terminal coloring
  lazy val rainbow                 = "pl.project13.scala"      %% "rainbow"                   % Versions.rainbow

  // java stuff
  lazy val festAssert              = "org.easytesting"          % "fest-assert"               % Versions.fest      % "test"

}

object ScaldingAssembly {
  import sbtassembly.Plugin._
  import AssemblyKeys._

  lazy val sbtAssemblySettings = assemblySettings ++ Seq(

    // Slightly cleaner jar name
    jarName in assembly <<= (name, version) { (name, version) => name + "-" + version + ".jar" },

    // Drop these jars, most of which are dependencies of dependencies and already exist
    // in Hadoop deployments or aren't needed for local mode execution. Some are older
    // versions of jars that collide with newer versions in the dependency graph!!
    excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
      val excludes = Set(
        "scala-compiler.jar",
        "jsp-api-2.1-6.1.14.jar",
        "jsp-2.1-6.1.14.jar",
        "jasper-compiler-5.5.12.jar",
        "minlog-1.2.jar", // Otherwise causes conflicts with Kyro (which Scalding pulls in)
        "janino-2.5.16.jar", // Janino includes a broken signature, and is not needed anyway
        "commons-beanutils-core-1.8.0.jar", // Clash with each other and with commons-collections
        "commons-beanutils-1.7.0.jar",
        "stax-api-1.0.1.jar",
        "mockito-all-1.9.0.jar",
        "asm-3.1.jar",
        "scalatest-2.0.jar"
      )
      cp filter { jar => excludes(jar.data.getName) }
    },

    mergeStrategy in assembly <<= (mergeStrategy in assembly) { old => {
        case PathList("META-INF", xs @ _*) =>
          xs map { _.toLowerCase } match {
            case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
            case _ => MergeStrategy.discard
          }
        case x => MergeStrategy.first
      }
    }
  )
}

object OculusBuild extends Build {
  import BuildSettings._
  import Dependencies._
  import ScaldingAssembly._

  lazy val root = Project("root", file("."), settings = buildSettings)
    .settings(
      net.virtualvoid.sbt.graph.Plugin.graphSettings: _*
    ) aggregate(common, scalding, downloader)


  lazy val common = Project(
    "common",
    file("common"),
    settings = buildSettings ++
      Seq(
        libraryDependencies ++= Seq(logback, hbase, scalaz, guava, rainbow, json4sJackson) ++ logging ++ testing
      ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings
  )

  lazy val scalding = Project(
    "scalding",
    file("scalding"),
    settings = buildSettings ++ sbtAssemblySettings ++
      Seq(
        libraryDependencies ++= hadoops ++ scaldingAll ++ akka2Full ++ testing ++ Seq(hPaste), // ++ powerMockAll,
        mainClass in assembly := Some("com.twitter.scalding.Tool")
//        mainClass in assembly := Some("pl.project13.scala.oculus.ScaldingJobRunner")
      ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings
  ) dependsOn (common)

  lazy val downloader = Project(
    "downloader",
    file("downloader"),
    settings = buildSettings ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(jsoup) ++ akka2Full ++ hadoops ++ testing ++ Seq(hPaste),
      mainClass in assembly := Some("pl.project13.scala.oculus.DownloaderRunner")
    ) ++ Seq(
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
        {
          case x if x.toString.toLowerCase contains "manifest.mf" => MergeStrategy.discard
          case x => MergeStrategy.first
        }
      }
    ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings
  ) dependsOn (common)
}
