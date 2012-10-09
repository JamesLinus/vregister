package net.brosbit4u.snippet.admin


import java.util.Date
import scala.xml.{ NodeSeq, Text, XML, Unparsed }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import _root_.net.liftweb.mapper.{ Ascending, OrderBy, By }
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

class AdminAddAnouncesSn {
	def addAnounce() = {
      var id = ""
	  var title = ""
	  var content = ""
	    
	  def save(){
	    val mainPageData = MainPageData.find(id) match {
          case Some(mainPageData) => mainPageData
          case _ => {
            val tmpMainPageData = MainPageData.create
            tmpMainPageData.key = Keys.anounce.toString
            tmpMainPageData
          }
        }
	    mainPageData.title = title.trim
	    mainPageData.content = content.trim
	    mainPageData.save
	  }
      
	  def delete(){
	    MainPageData.find(id) match {
	      case Some(mainPageData) => mainPageData.delete
	      case _ =>
	    }
	  }
	  
	  "#id" #> SHtml.text(id, id = _, "id"->"id","style"->"display:none;") &
	  "#title" #> SHtml.text(title, title = _, "id"->"title","maxlength"-> "40") &
	  "#content" #> SHtml.textarea(content, content = _, "id"->"content","rows"->"10") & 
	  "#submit" #> SHtml.submit("Zapisz" ,save, "onclick"->"return validate();") &
	  "#delete" #> SHtml.submit("Usuń", delete, "onclick"->"return areYouSure();")
  }
  
  def anounceList() = {
    val anounces = getMainPageDataWithAnounces
    "tr" #> anounces.map(anounce => {
    		<tr id={anounce._id.toString} ondblclick="setData(this);" ><td>{anounce.title}</td><td>{Unparsed(anounce.content)}</td></tr>
    	})
  }
  
	private def getMainPageDataWithAnounces = MainPageData.findAll(("key"->Keys.anounce.toString))
}