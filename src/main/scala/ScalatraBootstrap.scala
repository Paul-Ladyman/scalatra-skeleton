import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import bbc.cloudwatch.Reporter
import bbc.cloudwatch.Metrics.{global => metricsContext}
import bbc.cps.scalatraskeleton.Config._
import bbc.cps.scalatraskeleton.Servlets
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient
import org.scalatra._
import javax.servlet.ServletContext

import scala.concurrent.Future
import scala.concurrent.blocking


class ScalatraBootstrap extends LifeCycle {
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  override def init(context: ServletContext) {
    reportMetrics()

    Servlets().foreach(servletMapping => {
      val servlet = servletMapping._1
      val endpoint = servletMapping._2

      context.mount(servlet, endpoint)
    })
  }

  val reporter = new AtomicReference[Reporter]

  def reportMetrics() = {
    if (isCloudWatchEnabled) {
      val client = new AmazonCloudWatchClient {
        setRegion(Region.getRegion(Regions.EU_WEST_1))
      }
      reporter.set(new Reporter(metricsContext, "BBCApp/scalatraskeleton", client, environment))
      reporter.get.start(1, TimeUnit.MINUTES)
    }
  }

  override def destroy(context: ServletContext) {
    Option(reporter.get) map (_.stop())
  }
}
