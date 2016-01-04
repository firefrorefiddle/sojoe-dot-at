package controllers

import play.api._
import play.api.mvc._
import javax.measure.unit.SI.KILOGRAM
import javax.measure.quantity.Mass
import org.jscience.physics.model.RelativisticModel
import org.jscience.physics.amount.Amount

class Application extends Controller {

  def index = Action {
    Ok(views.html.index(""))
  }

  def cv = Action {
    Ok(views.html.cv(""))
  }

  

}
