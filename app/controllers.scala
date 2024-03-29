package controllers

import play._
import play.mvc._
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm.SqlParser._
import java.util.Date
import models._

object Application extends Controller {
  
  import views.Application._
  
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
                       params.get("noun").trim,
                       params.get("verb").trim,
                       params.get("obj").trim,
                       new Date()))
      flash += ("success" -> "Fact was added to the database")
    }
    Action(index)
  }
  
  def vote(dir: String) = {
    if(params.get("id") == null)            BadRequest
    else if(dir != "up" && dir != "down")   NotFound
    else {
      Vote.delete("remoteAddress={remoteAddress} AND factId={id}")
          .onParams(request.remoteAddress, params.get("id"))
          .execute()
      Vote.create(Vote(NotAssigned,
                       params.get("id").toInt,
                       if(dir == "up") { 1 } else { -1 },
                       new Date(),
                       request.remoteAddress))
      val score = SQL("SELECT SUM(value) FROM Vote WHERE factId={id}")
                    .onParams(params.get("id")).as(scalar[Long])
      Json("{\"score\":"+score+"}")
    }
  }

  def recentFacts = {
    val start: String = params.get("start") match {
      case null => "0"
      case x => x
    }
    val result: List[Fact~Long] =
      SQL("""
        SELECT Fact.*,
          COALESCE(SELECT SUM(value) FROM Vote WHERE factId=Fact.id, 0) as score
        FROM Fact ORDER BY addedAt DESC LIMIT {start},{factsPerPage}
      """).on("start" -> start,
              "factsPerPage" -> Constants.factsPerPage)
          .as(Fact ~< long("score") *)
    Json(if(result.isEmpty) "[]" else {
      "["+result.map(_ match {
        case fact ~ votes => fact.toJson(",\"votes\":"+votes)
      }).reduceLeft(_+","+_)+"]"
    })
  }

  def factCount = Json("{\"count\":"+Fact.count().single()+"}")

  def search = Forbidden
}
