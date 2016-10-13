
import controllers.HomeController
import org.scalatest.concurrent.ScalaFutures
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class FunctionalSpec extends PlaySpec with OneAppPerSuite with ScalaFutures {

  def dateIs(date: java.util.Date, str: String) = {
    new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  }

  def homeController = app.injector.instanceOf(classOf[HomeController])

  "HomeController" should {

    "redirect to the list on /" in {
      val result = homeController.index(FakeRequest())

      status(result) must equal(SEE_OTHER)
      redirectLocation(result) mustBe Some("/list")
    }
}