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
import _root_.net.liftweb.json.JsonDSL._

class BipSn extends UsersOperations {

  /** formuje menu szkoły wypisując tytuły strony - dodać linki*/
  def submenu() = {
    val departments = PageDepartment.findAll
    val pages = PageHead.findAll
    
    "#pagemenu" #> departments.map(department => { 
                             <h1>{ department.name }</h1> ++
                             <ul>{ pages.filter(page => page.department == department.name).map(page => { <li>
                             <a href={ "/bip/" + page._id.toString }>{ page.title }</a></li> }) }</ul> 
                             }) &
      "#adminmenu" #> <ul>
                        { if (isTeacher) <li><a href="editpage?id=0"><span>Dodaj artykuł</span></a></li> }
                      </ul>
  }


  //pokazuje konkretną stonę (nie aktualność)
  def pageContent() = {
    val id = S.param("id") openOr "0"

    val pageHead = PageHead.find(id) match {
      case Some(pageHead) => pageHead
      case _ => {
        val startPageId = ExtraData.getData(ExtraDataKeys.startPageKey.toString).drop(1)
        PageHead.find(startPageId) match {
          case Some(startPage) => startPage
          case _ => {
            val pageHeadList = PageHead.findAll
            if(pageHeadList.isEmpty) PageHead.create else pageHeadList.head
          }
        }
        
      }
    }
    
     val contentOption = ArticleContent.find(pageHead.content)
        "#pagedepartment *" #> Text(pageHead.department) &
        "#pagecontent" #> <div id="pagecontent">
        					<h1>{pageHead.title}</h1>
        					<div id="pagebody">{Unparsed(contentOption.getOrElse(ArticleContent.create).content)}</div>
        					<hr/>
        					<p id="pageinfo"><span class="fullname">{pageHead.authorName}</span>
        					<span class="date">{Formater.formatTime(new Date(pageHead._id.getTime()))}</span>
        					{if(isOwner(pageHead.authorId)) <span class="edit"><a href={"/editpage/"+pageHead._id.toString}>Edytuj</a></span> 
        					else <span></span> } </p>
        				  </div>
  }

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }   
  }

}
