import scala.scalajs.js
import org.scalajs.jquery.jQuery
import org.scalajs.dom.raw.HTMLInputElement

object ScalaJSDemo extends js.JSApp {

  def main() : Unit = {

    val startElem = jQuery("#start")

    if(startElem.length > 0) {

      val startStr = startElem(0)
	.asInstanceOf[HTMLInputElement]
	.value

      startStr match {
	case "scalaJSDemo1" => demov1.Animation.drawSystem()
	case "scalaJSDemo2" => demov2.Animation.drawSystem()
	case _ => println(s"Unknown starter $startStr")
      }
    }
  }
}
