package bbc.cps.scalatraskeleton.api

import bbc.cps.scalatraskeleton.api.mixin.{MetricsFilter, WhiteListFilter}
import org.scalatra.ScalatraServlet
import scala.util.control.NonFatal
import org.slf4j.LoggerFactory

trait BaseApi extends ScalatraServlet with MetricsFilter with WhiteListFilter {

  private val log = LoggerFactory getLogger getClass

  error {
    case NonFatal(e) => {
      log error e.getMessage
      log error e.getStackTraceString
      response.setStatus(500)
      s"Uncaught exception: ${e.getMessage}"
    }
  }
}
