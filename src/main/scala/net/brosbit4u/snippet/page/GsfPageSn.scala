/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import java.util.Date
import _root_.scala.xml.{ NodeSeq, Unparsed, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.brosbit4u.lib._
import _root_.net.liftweb.mapper.{ OrderBy, By, Descending, Ascending }
import _root_.net.liftweb.http.{ S }
import Helpers._

class GsfPageSn {

//tagi 
  def submenu() = {
    "ul *" #> GsfArticle.findAll.map(article => <li>
      <a href={"/gsf/" + article._id.toString}>{article.title}</a></li>) &
     "#adminmenu" #> <ul>
                        { if (isGsfEditor) <li><a href="gsfedit/0"><span>Dodaj artykuł</span></a></li> }
                      </ul>
  }
 


  
 def showArticle() = {
   var id = S.param("id").openOr("0")
   var gsfArticle =  GsfArticle.find(id) match {
     case Some(article) => article
     case _ => {
       var articles = GsfArticle.findAll
       if (articles.isEmpty) GsfArticle.create else articles.min(OrderPage)
     }
   }
   "#pagecontent" #> <div id="pagecontent">
        					<div id="pagebody">{Unparsed(gsfArticle.content)}</div>
        					<hr/>
        					<p>{if(isGsfEditor) <span class="edit"><a href={"/gsfedit/"+gsfArticle._id.toString}>Edytuj</a></span> 
        					else <span></span> } </p>
        				  </div>
  
 }


  private def isGsfEditor(): Boolean = {
    User.currentUser match {
      case Full(user) => (user.role.is == "g" || user.role.is == "a")
      case _ => false
    }   
  }

}
