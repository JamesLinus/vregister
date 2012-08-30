package net.brosbit4u.snippet.teacher

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.brosbit4u.lib.Formater
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import  _root_.net.liftweb.http.js.JsCmds._
 import  _root_.net.liftweb.http.js.JsCmd
 import  _root_.net.liftweb.http.js.JE._
 import  _root_.net.liftweb.mapper._


class OpinionsSn extends BaseTeacher {

  val classId = ClassChoose.is	
  
  def dataTable() = {
    val opinions = Opinions.findAll(("classId"->classId))
    "tr" #> opinions.map(opinion => {
      ".id *" #> opinion._id.toString &
      ".dateIn *" #> opinion.content.head.date &
      ".pupil *" #> opinion.pupilName &
       ".contentData *" #> Unparsed(opinion.content.head.content) &
      ".teacher *" #> opinion.teacherName
     
    })
  }
  
  def saveData() = {
    
    var id = ""
    var content = ""
    var pupilId = ""
    
    def save():JsCmd = {
      val opinion = Opinions.find(id).getOrElse(Opinions.create)

      if(id == "" || user.id.is == opinion.teacherId) {
            if (opinion.classId == 0L) opinion.classId = classId
            val entry = Entry(Formater.formatDate(new Date()), content.trim)
            opinion.content = entry::opinion.content
           
            if(id == "") {
              opinion.teacherId = user.id.is
              opinion.teacherName = user.getFullName
              val pupil = User.find(pupilId).getOrElse(User.create)
              opinion.pupilName = pupil.shortInfo
              opinion.pupilId = pupil.id.is
            }
            opinion.save
            JsFunc("$dTable.insertRow", opinion._id.toString).cmd
          }
      else Alert("Tylko właściciel może zmienić wpis!")
    }
    val pupils = User.findAll(By(User.classId, classId),By(User.role,"u")).filter(!_.scratched).map(user => {
    (user.id.toString, user.shortInfo)
    })
    
    val form = "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    		"#pupil" #> SHtml.select(pupils, Full(""), pupilId = _) &
    		"#contentData" #> SHtml.textarea(content, content = _ ) &
    		"#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image") andThen SHtml.makeFormsAjax
    		
    		
    "#loggedTeacher" #> <input id="loggedTeacher" value={User.currentUser.get.getFullName} 
        	 							type="text" style="display:none;"/> & 					
     "form" #> (in => form(in))
  }
  
}
