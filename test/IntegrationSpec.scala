import play.api.test._
import play.api.test.Helpers._
import org.fluentlenium.core.filter.FilterConstructor._
import org.scalatestplus.play.PlaySpec

import org.scalatestplus.play._

class IntegrationSpec extends PlaySpec {
  
  "Application" should {
    
    "work from within a browser" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/")
        
        browser.$("header h1").first.getText must equal("Play sample application — Fund database")
        browser.$("section h1").first.getText must equal("574 entries found")

        ???

      }
    }
    
  }
  
}