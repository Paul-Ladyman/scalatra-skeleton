package bbc.cps.collaboration.api.mixin

import com.codahale.metrics.Timer.Context
import org.scalatra.ScalatraServlet
import bbc.cloudwatch.Metrics.Implicits.global

trait MetricsFilter { this: ScalatraServlet =>

  lazy val requestsTimer = global.timer("request-time")
  lazy val statusCodes = Seq(2, 3, 4, 5).map(status => status -> global.meter(s"${status}xx-statuses")).toMap
  lazy val otherStatuses = global.meter("other-statuses")

  var context: Context = _

  before() {
    context = requestsTimer.time
  }

  after() {
    context.stop
    statusCodes.getOrElse(status / 100, otherStatuses).mark()
  }
}
