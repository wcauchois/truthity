package controllers

import play._
import play.mvc._
import play.mvc.results.Result
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm.SqlParser._
import java.util.Date
import models._

object Application extends Controller {
    
    import views.Application._

    val factsPerPage: Int = 10
    
    def index = html.index()

    def addFact = {
      val exists = !Fact.find("noun={noun} AND verb={verb} AND obj={obj}")
                        .on("noun" -> params.get("noun"),
                            "verb" -> params.get("verb"),
                            "obj" -> params.get("obj"))
                        .first().isEmpty
      if(exists) {
        flash += ("warning" -> "That fact already exists in the database")
      } else {
        Fact.create(Fact(NotAssigned,
                         params.get("noun"),
                         params.get("verb"),
                         params.get("obj"),
                         new Date()))
        flash += ("success" -> "Fact was added to the database")
      }
      Action(index)
    }
    
    def vote(dir: String): Result = {
      if(params.get("id") == "") {
        return BadRequest
      } else if(dir != "up" && dir != "down") {
        return NotFound
      }
      NoContent
    }

    def recentFacts = {
      val facts: List[Fact] = SQL("SELECT * FROM Fact ORDER BY addedAt DESC").as(Fact*)
      Json(if(facts.isEmpty) "[]" else {
        "["+facts.map(_.toJson()).reduceLeft(_+","+_)+"]"
      })
    }

    def search = Forbidden
}
