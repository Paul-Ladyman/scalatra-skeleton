package bbc.cps.collaboration

import bbc.cps.collaboration.service.{AmazonSNSClientDummy, AmazonSQSClientDummy}
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider}
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sqs.AmazonSQSClient
import org.json4s.JsonAST.JString
import org.json4s.jackson.JsonMethods
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Try

object Config {

  private val configFile = Try(io.Source.fromFile("/etc/vivo-collaboration/vivo-collaboration.json").mkString) getOrElse "{}"
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
    case "int" | "test" => s"https://vivo-collaboration.$environment.api.bbci.co.uk"
    case "live" => s"https://vivo-collaboration.api.bbci.co.uk"
  }

  log.info(s">>> Host: [$host]")

  object Mongo {
    val nodes = environment match {
      case "dev" => Seq("localhost")
      case "mgmt" | "int" => Seq("54.76.213.1")
      case "test" | "live" => getConfiguration("mongodb_ip_list").split(',').toSeq
    }

    log.info(s">>> Mongo Nodes: [$nodes]")

    val db = "vivo-collaboration"
    val postEditsCollection = "edits"
  }

  object NotificationAgent {
    val snsRegion = "sns.eu-west-1.amazonaws.com"
    val snsArn = environment match {
      case "dev" | "mgmt" | "int" => "arn:aws:sns:eu-west-1:169163488685:int-vivo-collaboration"
      case "test" => "arn:aws:sns:eu-west-1:169163488685:test-vivo-collaboration"
      case "live" => "arn:aws:sns:eu-west-1:303748928824:live-vivo-collaboration"
    }

    log.info(s">>> SNS Region: [$snsRegion]")
    log.info(s">>> SNS ARN: [$snsArn]")

    val awsCredentials = environment match {
      case "dev" => new EnvironmentVariableCredentialsProvider()
      case "mgmt" | "int" | "test" | "live" => new InstanceProfileCredentialsProvider()
    }
    val awsProxyConfig = new ClientConfiguration().withMaxErrorRetry(3).withProxyHost("www-cache.reith.bbc.co.uk").withProxyPort(80)

    def apply() = environment match {
      case "dev" | "mgmt" => AmazonSNSClientDummy()
      case "int" | "test" | "live" => new AmazonSNSClient(awsCredentials, new ClientConfiguration().withMaxErrorRetry(3)) { setEndpoint(snsRegion) }
    }
  }

  object QueueAgent {
    val sqsUrl = environment match {
      case "dev" => "https://sqs.eu-west-1.amazonaws.com/169163488685/vivo-collaboration-schedule"
      case "int" => "https://sqs.eu-west-1.amazonaws.com/169163488685/int-vivo-collaboration-schedule"
      case "test" => "https://sqs.eu-west-1.amazonaws.com/169163488685/test-vivo-collaboration-schedule"
      case "live" => "https://sqs.eu-west-1.amazonaws.com/303748928824/live-vivo-collaboration-schedule"
      case _ => ""
    }

    log.info(s">>> SQS URL: [$sqsUrl]")

    val sqsClient = environment match {
      case "dev" | "mgmt" => AmazonSQSClientDummy()
      case "int" | "test" | "live" => new AmazonSQSClient {setEndpoint("sqs.eu-west-1.amazonaws.com")}
    }

    def apply(): AmazonSQSClient = sqsClient
  }

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
      case "test" | "live" => Some(s"/usr/lib/vivo-collaboration/conf/whitelist/$environment.txt")
    }
    val emails: Option[Set[String]] = path map { Source.fromFile(_).getLines().map(_.toLowerCase.trim).toSet }
    log.info(s">>> White list [${path getOrElse s"OFF in $environment"}]")
  }
}