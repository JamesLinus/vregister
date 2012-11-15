package net.brosbit4u.snippet.teacher

import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import net.brosbit4u.model._

class MarksSn extends BaseTeacher {
  val classId = ClassChoose.is	
  
  def showTable() = {
    
    "#thead" #> <tr><th>Nr</th><th>Nazwisko i imię</th>
    			{}<th>Średnia</th><th>Semestr</th></tr> & 
    "tbody *" #> <tr></tr> 
  }
  
  def saveOneMark() = {
    var mark = ""
    var columnId = ""
    var userId = ""
    var semestr = ""
    var subjectId = ""
      
    def save(){
      
    }
    
    val form = "#subjectId" #> SHtml.text(subjectId, subjectId = _) &
    "#semestr" #> SHtml.text(semestr, semestr = _) &
    "#onemark" #> SHtml.text(mark, mark = _) &
    "#columnNr" #> SHtml.text(columnId, columnId = _) &
    "#userId" #> SHtml.text(userId, userId = _) &
    "#saveMark" #> SHtml.submit("OK", save) andThen SHtml.makeFormsAjax
    
    "#form" #> (in => form(in)) 
  }
  
  def saveSemestrMark() = {
    var mark = ""
    var userId = ""
    var semestr = ""
    var subjectId = ""
      
    def save(){
      
    }
    val marks = (1 to 6).map(i => (i.toString, i.toString))
    
    "#subjectIdSem" #> SHtml.text(subjectId, subjectId = _) &
    "#semestrSem" #> SHtml.text(semestr, semestr = _) &
    "#semmark" #> SHtml.select(marks, Full(mark), mark = _) &
    "#userIdSem" #> SHtml.text(userId, userId = _) &
    "#saveMarkSem" #> SHtml.submit("OK", save)
    
  }

  def saveTableHeader() = {
    var subjectId = ""
    var columnId = ""
    var semestr = ""
    var symbol = ""
    var userId = ""
    var weight = "1"
    
    def save() {
      
    }
    var weights = (1 to 9).map(i => (i.toString, i.toString))
    
    "#subjectIdCol" #> SHtml.text(subjectId, subjectId = _) &
    "#columnNrCol" #> SHtml.text(columnId, columnId = _) &
    "#semestrCol" #> SHtml.text(semestr, semestr = _) &
    "#symbol" #> SHtml.text(symbol, symbol = _) &
    "#weight" #> SHtml.select(weights, Full(weight), weight = _) &
    "#userIdCol" #> SHtml.text(userId, userId = _) &
    "#saveHeader" #> SHtml.submit("OK", save)
  }
}
