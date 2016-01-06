package demov2

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas
import org.scalajs.jquery.jQuery
import scala.scalajs.js.annotation.JSExport
import Math._

import twodee.shapes._

object Animation {

  import demov2.Canvas._
  import demov2.Canvas.Geometry._

  def circle(center: CoordsPoint, radius: Double, colorF: ColorF) {
    draw(BoundingBox(center + CoordsPoint(-radius, radius), 
		     center + CoordsPoint(radius, -radius)),	 
	 (p => {
	   val relP = p + CoordsPoint(center.x * -1, center.y * -1)
	   if(sqrt(relP.x*relP.x + relP.y*relP.y) <= radius) { 
	     Some(colorF(p))
	   } else {
	     None
	   }}))
  }
  
  def gradientVal(x: Double, kIntv: (Double, Double), vIntv: (Double, Double)) =
    kIntv match {
      case (minK, maxK) => vIntv match {
	case (minV, maxV) =>
	  (x-minK) * (maxV-minV) / (maxK-minK) + minV
      }
    }
    
  def sun() {
    val radius = 0.15
    circle(CoordsPoint(0,0), radius,
	   (p => {
	     val d = sqrt(p.x*p.x+p.y*p.y)
	     val r = 255
	     val g = 240
	     val b = round(gradientVal(d, (0,radius), (150,100)))
	     s"rgb($r,$g,$b)"
	   }))
  }

  def earth() {
    val radius = 0.05
    val t = new java.util.Date().getTime/1000.0
    val c = CoordsPoint(cos(t), sin(t))
    circle(CoordsPoint(cos(t)*0.8, sin(t)*0.8), radius, (_ => "blue"))
  }

  def drawSystem() {
    scala.scalajs.js.Dynamic.global.window.requestAnimationFrame(() => drawSystem)
    ctx.clearRect(0,0,canvas.width,canvas.height)
    setSize()
    background()
    sun()
    earth()
  }
}
