package util

import bbc.cps.scalatra-skeleton.Servlets
import bbc.cps.scalatra-skeleton.service.InactiveEditsPurgeQueueClient
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

import scala.concurrent.Future

object ScalatraSkeletonTestServer {
  private val port = 8080
  private val server = new Server(port)
  private val context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  Servlets().foreach(servletMapping => {
    val servlet = servletMapping._1
    val endpoint = servletMapping._2
    val servletHolder = new ServletHolder(servlet)
    context.addServlet(servletHolder, endpoint)
  })
  context.setResourceBase("src/main/webapp")
  Future { InactiveEditsPurgeQueueClient().processQueue() }
  server.start()
}
