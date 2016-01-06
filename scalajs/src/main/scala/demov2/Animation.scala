package demov2

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas
import org.scalajs.jquery.jQuery
import scala.scalajs.js.annotation.JSExport
import Math._

import twodee.geometry._
import twodee.shapes._
import twodee.transform._

object Animation {

  import Canvas.Anim
  import Canvas.ColoredShape

  def gradientVal(x: Double, kIntv: (Double, Double), vIntv: (Double, Double)) =
    kIntv match {
      case (minK, maxK) => vIntv match {
	case (minV, maxV) =>
	  (x-minK) * (maxV-minV) / (maxK-minK) + minV
      }
    }
    
  val sun = Anim(() => {
    val radius = 0.15
    val colorF = ((p: Vector) => {
      val d = sqrt(p.x*p.x+p.y*p.y)
      val r = 255
      val g = 240
      val b = round(gradientVal(d, (0,radius), (150,100)))
      s"rgb($r,$g,$b)"
    })
    
    ColoredShape(Circle(Scale(0.15)), colorF)
  })

  val earth = Anim(() => {
    val radius = 0.05
    val t = new java.util.Date().getTime/1000.0
    val coords = Vector(cos(t)*0.8, sin(t)*0.8)
    val shape  = Circle.unit scale radius translate coords
    ColoredShape(shape, (_ => "blue"))
  })

  def animate() {
    scala.scalajs.js.Dynamic.global.window.requestAnimationFrame(() => animate)
    Canvas.redraw()
  }

  def drawSystem() {
    Canvas.init()
    Canvas.add("sun", sun)
    Canvas.add("earth", earth)
    animate()
  }
}
