package net.brosbit4u.snippet.admin

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import _root_.net.liftweb.json.JsonDSL.{pair2jvalue}
import _root_.net.liftweb.json.JsonAST._


class AdminSetStartPageSn {
/* Annoyed problem with implicide conversion form ID inside ExtraData class changing ID in data 
 * field into ObjectID(ID). To avoid need to add prefix to ID string i.e #
 * */
	def change() = {

	  var ID = ""
	  var title = ""
	  ID = ExtraData.getData(ExtraDataKeys.startPageKey.toString)
	  if (!ID.isEmpty && ID(0) == '#') ID = ID.drop(1)
	  val startPage = PageHead.find(ID).getOrElse(PageHead.create)
	  title = startPage.title
	    
	  def save(){
	    val linkElements = ID.trim.split('/')
	    ID = if(linkElements.isEmpty) "" else linkElements.last.trim
	    val key = ExtraDataKeys.startPageKey.toString
	    PageHead.find(ID) match {
	      case Some(pageHead) => {
	          
	    	  ExtraData.findAll( JObject(List(JField("key", JString(key)))) ) match {
	    	    case extraData::list => {
	    	      extraData.data = "#" + ID
	    	      extraData.save
	    	    } 
	    	    case _ => {
	    	      val extraData = ExtraData.create
	    	      extraData.key = key
	    	      extraData.data =  "#" + ID
	    	      extraData.save
	    	    }
	    	  }
	       }
	      case _ => S.notice("Strona nie istnieje")
	    } 
	  }  
	  "#title *"  #> title & 
	  "#id" #> SHtml.text(ID, ID = _) & 
	  "#save" #> SHtml.submit("Zapisz" ,save) 
  }

}