import sbt._
import sbt.{Build => SbtBuild}
import sbt.Keys._
import org.scalatra.sbt._
import com.earldouglas.xsbtwebplugin.PluginKeys._
import com.earldouglas.xsbtwebplugin.WebPlugin._
import templemore.sbt.cucumber.CucumberPlugin
import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends SbtBuild {

  val Organization = "bbc"
  val Name = "scalatra-skeleton"
  val Version = Option(System.getenv("BUILD_VERSION")) getOrElse "DEV"
  val ScalaVersion = "2.10.6"
  val ScalatraVersion = "2.3.1"

  val JettyPort = sys.props.get("jetty.port") map (_.toInt) getOrElse 8080

  val dependencies = Seq(

    // core
    "org.scalatra" %% "scalatra" % ScalatraVersion,
    "ch.qos.logback" % "logback-classic" % "1.0.13" % "runtime",
    "org.scalatra" %% "scalatra-json" % "2.3.0",
    "org.json4s" %% "json4s-jackson" % "3.2.9",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.3",
    "bbc.shared" %% "cloudwatch-metrics" % "1.0.1",
    "bbc.shared" % "bbc-scala-whitelist" % "1.0.3",

    // test
    "org.scalatest" %% "scalatest" % "2.1.0" % "test",
    "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "info.cukes" %% "cucumber-scala" % "1.1.6" % "compile,test",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.3" % "test",

    // jetty
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container;compile"
  )

  lazy val project = Project(
    Name,
    file("."),
    settings =
      Defaults.coreDefaultSettings ++
      ScalatraPlugin.scalatraWithJRebel ++
      assemblySettings ++
      Seq(CucumberPlugin.cucumberSettings: _*) ++
      Seq(
        CucumberPlugin.cucumberFeaturesLocation := "cucumber",
        CucumberPlugin.cucumberJsonReport := true,
        CucumberPlugin.cucumberStepsBasePackage := "steps",
        unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "test/fixtures")
      ) ++
      Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7", "-language:implicitConversions")) ++
      Seq(
        organization := Organization,
        name := Name,
        version := Version,
        scalaVersion := ScalaVersion,
        resolvers += Classpaths.typesafeReleases,
        jarName in assembly := s"$Name.jar",
        mainClass in assembly := Some("Launcher"),
        mergeStrategy in assembly := {
          case "mime.types" => MergeStrategy.filterDistinctLines
          case x =>
            val oldStrategy = (mergeStrategy in assembly).value
            oldStrategy(x)
        },
        libraryDependencies ++= dependencies,
        port in container.Configuration := JettyPort
      ) ++
      Seq(
        resolvers += "Local Ivy repository" at "file://" + Path.userHome + "/.ivy2/local",
        resolvers += "BBC Forge Maven Releases" at "https://dev.bbc.co.uk/maven2/releases/",
        resolvers += "BBC Forge Maven Snapshots" at "https://dev.bbc.co.uk/maven2/snapshots",
        resolvers += "BBC Forge Artifactory" at "https://dev.bbc.co.uk/artifactory/repo",
        resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
      )
  )
}
