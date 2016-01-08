package demov2

import scala.collection.mutable.HashMap

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas
import org.scalajs.jquery.jQuery
import scala.scalajs.js.annotation.JSExport
import Math._

import twodee.shapes._
import twodee.geometry._

object Canvas {

  type ColorF = Vector => String

  case class ColoredShape(shape: Shape, color: ColorF)
  case class Anim(paintF: (() => ColoredShape))

  private var objects: HashMap[String, (Option[Box], Anim)] = HashMap.empty
  def add(id: String, anim: Anim) = objects += id -> (None, anim)
  
  type Context = dom.CanvasRenderingContext2D

  val canvas: Canvas = jQuery("#canvas")(0).asInstanceOf[Canvas]
  val ctx = canvas.getContext("2d").asInstanceOf[Context]
  setSize()
  val raster = Raster(canvas.width, Box.stdBox)

  def setSize() {
    val minW = 300
    val minH = 300
    val maxW = 300
    val maxH = 300

    val maxProp = 0.3
    val screenW = round(dom.window.screen.availWidth  * maxProp).toInt
    val screenH = round(dom.window.screen.availHeight * maxProp).toInt

    canvas.width  = min(maxW, max(minW, min(screenW, screenH)))
    canvas.height = min(maxH, max(minH, min(screenW, screenH)))
  }

  private def rasterBox(box: Box) =
    (raster.fromPoint(box.upperLeft), raster.fromPoint(box.lowerRight))

  private def background(box: Box) {
    val (ulr, lrr) = rasterBox(box)
    ctx.fillStyle = "black"
    ctx.fillRect(ulr.x, ulr.y, lrr.x, lrr.y)
  }

  def init() {
    background(Box.stdBox)
  }

  def draw(anim: Anim): Box = {

    val s = anim.paintF()
    val box = Box.boundingBox(s.shape.boundingPoly)

    val ul = raster.fromPoint(box.upperLeft)
    val lr = raster.fromPoint(box.lowerRight)

    for(i <- ul.x until (lr.x+1)) {
      for(j <- ul.y until (lr.y+1)) {
	val p = raster.toPoint(RasterPoint(i,j))
	if(s.shape.inside(p)) {
	  ctx.fillStyle = s.color(p)
	  ctx.fillRect(i,j,1,1)
	}
      }
    }

    box
  }

  def redraw() {

    def clearBox(box: Box) {
      background(box)
    }

    for((_, (oldbox,_)) <- objects) {
      oldbox match {
	case Some(box) => clearBox(box)
	case None =>
      }
    }

    val t = new java.util.Date().getTime()

    objects = objects map {
      case (id, (box, anim)) => id -> (Some(draw(anim)), anim)
    }

    val u = new java.util.Date().getTime()
    println(u-t)
  }
}
