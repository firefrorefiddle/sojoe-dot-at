package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

import play.twirl.api.Html

import scala.io._

import org.pegdown.PegDownProcessor

class Application extends Controller {

  val pdProc = new PegDownProcessor

  def index() = page("index")

  def page(name: String) = Action {

    try {
      val src = Source.fromFile(Play.getFile(s"conf/markdown/$name.md"))
      val html = Html(pdProc.markdownToHtml(src.mkString))
      val html2 = Application.specialTreatment(name) match {
	case None    => html
	case Some(f) => f(html)
      }
      Ok(views.html.main(name, name)(html2))
    } catch {
      case ex: java.io.FileNotFoundException => {
	NotFound
      }
    }
  }
}

object Application {

  def siteName = "sojoe.at"

  def pageTitle(title: String) =
    if(title == "") siteName
    else title

  def specials = Map.empty

  def specialTreatment(name: String): Option[(Html => Html)] = {
    specials get name
  }
}
