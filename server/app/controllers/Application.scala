package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.twirl.api.Html

import scala.io._

import org.pegdown.PegDownProcessor

class Application extends Controller {

  val pdProc = new PegDownProcessor

  def index() = page("index")

  def page(name: String) = Action {
    implicit request => {

      val langs = request.acceptLanguages.map(_.code) ++ List("en", "de");

      def tryLang(lang: String): Option[Source] = {
        try {
          val src = Source.fromFile(Play.getFile(s"conf/markdown/$lang/$name.md"))
          Some(src)
        } catch {
          case ex: java.io.FileNotFoundException => {
            None
          }
        }
      }

      langs flatMap tryLang headOption match {
        case None => NotFound("Not found")
        case Some(src) =>
          Ok(views.html.main(name, name)(Html(pdProc.markdownToHtml(src.mkString))))
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
