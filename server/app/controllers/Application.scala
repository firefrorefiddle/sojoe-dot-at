package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def cv = Action {
    Ok(views.html.cv())
  }

  def projects = Action {
    Ok(views.html.projects())
  }

  def tech = Action {
    Ok(views.html.tech())
  }

  def about = Action {
    Ok(views.html.about())
  }

  def thisSite = Action {
    Ok(views.html.thisSite())
  }

  def contact = Action {
    Ok(views.html.contact())
  }

}

object Application {
  def siteName = "sojoe.at"
  def pageTitle(title: String) =
    if(title == "") siteName
    else title
}