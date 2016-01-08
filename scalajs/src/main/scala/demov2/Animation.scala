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
    
  val sun = {
    val radius = 0.15
    val shape = Circle(Scale(0.15))

    val colorF = ((p: Vector) => {
      val d = sqrt(p.x*p.x+p.y*p.y)
      val r = 255
      val g = 240
      val b = round(gradientVal(d, (0,radius), (150,100)))
      s"rgb($r,$g,$b)"
    })
    Anim(() => {
      ColoredShape(shape, colorF)
    })
  }

  val spaceShipShape = {
    def circledRect(length: Double, width: Double) = {
      val w = width
      val r = w/2
      val sl = length-2*r
      val end = Circle.unit scale (r)
      val middle = RegularPolygon.square.scale(sl, w)

      end.translate( length/2, 0) + 
      middle + 
      end.translate(-(length/2), 0)
    }

    val disc   = Circle.unit scale 0.05
    val body   = circledRect(0.1,  0.025)
    val engine = circledRect(0.08, 0.02)
    
    body +
    disc.translate(0.05,0) +
    engine.translate(-0.07,  0.025) +
    engine.translate(-0.07, -0.025)
  }

  val spaceShip = {
    val shape = spaceShipShape
    val color = ((_:Vector) => "grey")
    Anim(() => {
      val t = new java.util.Date().getTime/1000.0
      val coords = Vector(cos(t)*0.8, sin(t)*0.8)
      ColoredShape(
	shape.rotateAroundOrigin(Rad(t+PI/2)).translate(coords),
	color)
    })
  }

  def animate() {
    scala.scalajs.js.Dynamic.global.window.requestAnimationFrame(() => animate)
    Canvas.redraw()
  }

  def drawSystem() {
    Canvas.init()
    Canvas.add("sun", sun)
    Canvas.add("ship", spaceShip)
    animate()
  }
}
