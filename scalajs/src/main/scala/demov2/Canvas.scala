package demov2

import scala.collection.immutable.HashMap

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas
import org.scalajs.jquery.jQuery
import scala.scalajs.js.annotation.JSExport
import Math._

import twodee.shapes.Box
import twodee.shapes.Shape

object Canvas {

  object Geometry {
    
    case class CoordsPoint(x: Double, y: Double) {
      def +(q: CoordsPoint) = CoordsPoint(x + q.x, y + q.y)
    }
    
    case class RasterPoint(x: Int, y: Int) {
      def +(q: RasterPoint) = RasterPoint(x + q.x, y + q.y)
    }
    
    trait ToRaster[T] {
      def apply(t: T) : RasterPoint
    }

    trait ToCoords[T] {
      def apply(t: T) : CoordsPoint
    }

    implicit object CoordsToRaster extends ToRaster[CoordsPoint]{
      def apply(p: CoordsPoint): RasterPoint = {
	val halfw = canvas.width/2.0
	val halfh = canvas.height/2.0
	RasterPoint(round(halfw + halfw * p.x).toInt,
		    round(halfh + halfh * p.y * -1).toInt)
      }
    }
  
    implicit object RasterToCoords extends ToCoords[RasterPoint]{
      def apply(p: RasterPoint): CoordsPoint = {
	val halfw = canvas.width/2.0
	val halfh = canvas.height/2.0
	CoordsPoint((p.x.toDouble - halfw) / canvas.width*2,
		    ((p.y.toDouble - halfh) / canvas.height * -2))
      }
    }
    
    case class BoundingBox(upperLeft: CoordsPoint, lowerRight: CoordsPoint)
  }

  var objects: HashMap[String, (_ => Shape)] = HashMap.empty
  
  import Geometry._

  type ColorF = CoordsPoint => String
  
  type Context = dom.CanvasRenderingContext2D

  val canvas: Canvas = jQuery("#canvas")(0).asInstanceOf[Canvas]
  val ctx = canvas.getContext("2d").asInstanceOf[Context]

  def setSize() {
    val minW = 100
    val minH = 100
    val maxW = 500
    val maxH = 500

    val maxProp = 0.3
    val screenW = round(dom.window.screen.availWidth  * maxProp).toInt
    val screenH = round(dom.window.screen.availHeight * maxProp).toInt

    canvas.width  = min(maxW, max(minW, min(screenW, screenH)))
    canvas.height = min(maxH, max(minH, min(screenW, screenH)))
  }

  def background() {
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, canvas.width, canvas.height)
  }

  def draw(box: BoundingBox, colorF : CoordsPoint => Option[String])
	  (implicit toR: ToRaster[CoordsPoint], toC: ToCoords[RasterPoint]) = {

    val ul = toR(box.upperLeft)
    val lr = toR(box.lowerRight)

    for(i <- ul.x until (lr.x+1)) {
      for(j <- ul.y until (lr.y+1)) {
	colorF(toC(RasterPoint(i,j))) match {
	  case Some(color) => {
	    ctx.fillStyle = color
	    ctx.fillRect(i,j,1,1)
	  }
	  case None =>
	}
      }
    }
  }

  def redraw() {
  }
}
