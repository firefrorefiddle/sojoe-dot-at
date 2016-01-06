import scala.scalajs.js

object ScalaJSDemo extends js.JSApp {
  def main() : Unit = {
    scala.scalajs.js.Dynamic.global.window.requestAnimationFrame(() => Animation2.drawSystem)
  }
}
