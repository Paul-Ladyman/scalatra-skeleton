import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object Launcher extends App {

  val port = sys.props.get("jetty.port") map (_.toInt) getOrElse 8080

  val server = new Server(port)
  val context = new WebAppContext

  context.setContextPath("/")
  context.setResourceBase("src/main/webapp")
  context.addEventListener(new ScalatraListener)
  context.addServlet(classOf[DefaultServlet], "/")

  server.setHandler(context)
  server.start()
  server.join()
}
