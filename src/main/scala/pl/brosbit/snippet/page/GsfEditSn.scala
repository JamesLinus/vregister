/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import _root_.scala.xml.{ NodeSeq, Unparsed }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._

class GsfEditSn {

  def editPage() = {
    val orderTuples = List(("1","1"),("2","2"))
    
    var id = S.param("id").openOr("")
    var title = ""
    var order = "2"
    var content = ""
      
    if(id.length > 11) GsfArticle.find(id) match {
          case Some(article) => {
            title = article.title
            content = article.content
            order = article.order.toString
          }
          case _ => 
    }
    
    def save(){
     val article = GsfArticle.find(id).getOrElse(GsfArticle.create)
      if(isGsfEditor){
          article.title = title
          article.order = order.toInt
          article.content = content
          article.save
         } 
      else  S.redirectTo("/gsf/id=" + article._id.toString)
      S.redirectTo("/gsf/id=" + article._id.toString)
    }

    def discard() {
      S.redirectTo("/gsf" )
    }
    
    def delete(){
     {if(id.length > 11) if(isGsfEditor)  deleteObjectById(id)}
     S.redirectTo("/gsf" )
    }
    
    "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    "#title" #> SHtml.text(title, in => title = in.trim) &
    "#order" #> SHtml.select(orderTuples,Full(order), order = _) &
    "#editor" #> SHtml.textarea(content, in => content = in.trim) &
    "#save" #> SHtml.submit("Zapisz", save, "onclick"->"return isValid()") &
    "#delete" #> SHtml.submit("Usuń", delete,  "onclick"->"return confirm('Na pewno usunąć wpis?');") &
    "#discard" #> SHtml.submit("Porzuć", discard, "onclick"->"return confirm('Na pewno porzucić bez zapisu?');") 
     
  }
  
  private def deleteObjectById(id:String) {
        GsfArticle.find(id) match {
          case Some(article) => {
            article.delete
          }
          case _ => 
        }
  }
  
    private def getFullLink(id:String) = "/gsf/id=" + id
    
    private def isGsfEditor(): Boolean = {
    User.currentUser match {
      case Full(user) => (user.role.is == "g" || user.role.is == "a")
      case _ => false
    }   
  }

}


