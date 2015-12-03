package steps

import com.ning.http.client.Response
import cucumber.api.scala.{EN, ScalaDsl}
import dispatch.{Http, Req}
import bbc.cps.scalatraskeleton.Servlets
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait ApiSteps extends ScalaDsl with EN {

  def response = ResponseHolder.response
  def responseBody = ResponseHolder.response.getResponseBody

  def get(request: Req) = {
    val req = Http(request.setHeader("Accept", "application/json").setMethod("GET"))
    ResponseHolder.response = Await.result(req, 10.seconds)
  }

  def put(request: Req) = {
    val req = Http(request.setHeader("Content-Type", "application/json").setMethod("PUT").setBody(RequestHolder.body))
    ResponseHolder.response = Await.result(req, 10.seconds)
  }

  ApiTestServer
}

object RequestHolder {
  var body: String = _
}

object ResponseHolder {
  var response: Response = _
}

object ApiTestServer {
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
  server.start()
}
