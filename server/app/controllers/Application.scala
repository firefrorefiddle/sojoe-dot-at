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
      val src = Source.fromFile(Play.getFile(s"markdown/$name.md"))
      Ok(views.html.main(name, name)(Html(pdProc.markdownToHtml(src.mkString))))
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
}
