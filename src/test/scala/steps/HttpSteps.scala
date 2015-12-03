package steps

import bbc.cps.scalatraskeleton.Config
import dispatch.url
import org.scalatest.MustMatchers

object HttpSteps extends ApiSteps with MustMatchers {

  val host = Config.host

  When("""^I "(.*?)" "(.*?)"$"""){ (method: String, endpoint: String) =>
    val req = url(s"$host$endpoint")
    method match {
      case "GET" => get(req)
      case "PUT" => put(req)
      case _ =>
    }
  }

  Then("""^the response status code is (\d+)$"""){ (status: Int) =>
    response.getStatusCode mustBe status
  }

}
