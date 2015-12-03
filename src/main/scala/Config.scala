package bbc.cps.scalatraskeleton

import org.json4s.JsonAST.JString
import org.json4s.jackson.JsonMethods
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Try

object Config {

  private val configFile = Try(io.Source.fromFile("/etc/scalatraskeleton/scalatraskeleton.json").mkString) getOrElse "{}"
  private lazy val config = JsonMethods.parse(configFile)
  private val log = LoggerFactory getLogger getClass

  lazy val environment = {
    val env = config \ "environment" match {
      case JString(value) => value
      case _ => sys.env.getOrElse("SERVER_ENV", "dev")
    }
    log.info(s">>> Environment: [$env]")
    env
  }

  lazy val isCloudWatchEnabled = Seq("int", "test", "live") contains environment

  def getConfiguration(key: String) = {
    config \ "configuration" \ key match {
      case JString(value) => value
      case value => throw new Exception(s"Could not retrieve config [$key], found [$value] of type [${value.getClass}]")
    }
  }

  val host = environment match {
    case "dev" | "mgmt" => "http://localhost:8080"
    case "int" | "test" => s"https://scalatraskeleton.$environment.api.bbci.co.uk"
    case "live" => s"https://scalatraskeleton.api.bbci.co.uk"
  }

  log.info(s">>> Host: [$host]")

  environment match {
    case "dev" =>
    case "mgmt" =>
      System.setProperty("javax.net.ssl.trustStore", "/etc/pki/cosmos/current/client.jks")
      System.setProperty("javax.net.ssl.keyStore", "/etc/pki/tls/private/client.p12")
      System.setProperty("javax.net.ssl.keyStoreLocation", "/etc/pki/tls/private/client.p12")
      System.setProperty("javax.net.ssl.keyStorePassword", "client")
      System.setProperty("javax.net.ssl.keyStoreType", "PKCS12")
    case "int" | "test" | "live" =>
      System.setProperty("javax.net.ssl.trustStore", "/etc/pki/java/cacerts")
      System.setProperty("javax.net.ssl.keyStore", "/etc/pki/tls/private/client.p12")
      System.setProperty("javax.net.ssl.keyStoreLocation", "/etc/pki/tls/private/client.p12")
      System.setProperty("javax.net.ssl.keyStorePassword", "client")
      System.setProperty("javax.net.ssl.keyStoreType", "PKCS12")
  }

  object Whitelist {
    private val path: Option[String] = environment match {
      case "dev" | "mgmt" | "int" => None
      case "test" | "live" => Some(s"/usr/lib/scalatraskeleton/conf/whitelist/$environment.txt")
    }
    val emails: Option[Set[String]] = path map { Source.fromFile(_).getLines().map(_.toLowerCase.trim).toSet }
    log.info(s">>> White list [${path getOrElse s"OFF in $environment"}]")
  }
}
