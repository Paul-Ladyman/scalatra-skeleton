package bbc.cps.scalatraskeleton.api.mixin

import bbc.cps.scalatraskeleton.Config
import bbc.scala.whitelist.{Whitelist, Certificate}
import org.scalatra.ScalatraServlet
import org.slf4j.LoggerFactory

trait WhiteListFilter {
  this: ScalatraServlet =>

  protected val whitelist = new Whitelist(Config.Whitelist.emails)
  protected val env = Config.environment

  private val log = LoggerFactory getLogger getClass

  before() {
    env match {
      case "dev" | "mgmt" | "int" =>
      case "test" | "live" => (request.getMethod, request.getPathInfo) match {
        case ("GET", "/status") =>
        case _ => request.headers.get("SSLClientCertSubject") match {
          case Some(subject) =>
            val email = Certificate.fromSubjectString(subject).email.toLowerCase
            if (!whitelist.isAuthorised(email)) {
              val message = s"Unauthorized request from [$email]"
              log warn message
              halt(403, message)
            }
          case None =>
            val message = "Wrong request (missing certificate)"
            log warn message
            halt(401, message)
        }
      }
    }
  }
}
