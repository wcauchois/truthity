package models

import play._
import play.db.anorm._
import play.db.anorm.defaults._
import java.util.Date

case class Fact(
    id: Pk[Long], noun: String, verb: String, obj: String, addedAt: Date) {
  
  def toJson(extra: String = ""): String = {
    val stringify = (key: String, value: String) => "\""+key+"\":\""+value+"\""
    "{"+stringify("id", id.toString)+","+stringify("noun", noun)+","+
        stringify("verb", verb)+","+stringify("obj", obj)+extra+"}"
  }
}

object Fact extends Magic[Fact]
