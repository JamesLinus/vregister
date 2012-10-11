/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import _root_.scala.xml.{ NodeSeq }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._
import _root_.net.brosbit4u.lib.{ Formater }
import java.util.Date
import _root_.net.liftweb.json.JsonDSL._

class ForumEditSn extends UsersOperations with UpdateMainPageInfo {
  

  
  def editThread() = {
    var id = ""
    var departmentName = ""
    var title = ""
    var content = ""

    id = S.param("id").openOr("")
    
    ForumThreadHead.find(id) match {
      case Some(threadHead) => {
        departmentName = threadHead.department
        title = threadHead.title
        val threadContent = ForumThreadContent.find(threadHead.content).getOrElse(ForumThreadContent.create)
        content = threadContent.content
      }
      case _ => {
        
      }
    }
    if(departmentName == ""){
      departmentName = S.param("dep").openOr("")
    }
    
    def save() {
    	val threadHead = ForumThreadHead.find(id).getOrElse(ForumThreadHead.create)
    	val threadContent = ForumThreadContent.find(threadHead.content).
    				getOrElse(ForumThreadContent.create)
    	val user = User.currentUser.get
    	if( id == "" || threadHead.authorId == user.id.is || isAdmin ){
    	  threadHead.title = title
    	  threadContent.content = content
    	  threadHead.department = departmentName
    	  if( id == ""){
    	     threadHead.authorId = user.id.is
    	     threadHead.authorName = user.getFullName
    	     super.updateForumInfo(Formater.formatTime(new Date(threadHead._id.getTime)),
    	         title, threadHead._id.toString)
    	  }
    	  threadHead.content = threadContent._id
    	  
    	  threadHead.save
    	  threadContent.save
    	}  	 
        S.redirectTo("/forumpost/"+ threadHead._id.toString)
    }
    
    def delete(){
      ForumThreadHead.find(id) match {
        case Some(threadHead) => if( threadHead.authorId == User.currentUser.get.id.is || isAdmin) {
            ForumThreadContent.find(threadHead.content) match {
              case Some(threadContent) => threadContent.delete
              case _ =>       
            }
            threadHead.delete
        }
        case _ => 
      }
      S.redirectTo("/forum/")
    }
    val departments = ForumDepartment.findAll.map(department => (department.name, department.name))
    
    "#id" #> SHtml.text(id, id = _, "type" -> "hidden") &
      "#title" #> SHtml.text(title, x => title = x.trim, "id" -> "title") &
      "#department" #> SHtml.select(departments, Full(departmentName), departmentName = _ ) &
      "#content" #> SHtml.textarea(content, x => content = x, "id" -> "content") &
      "#save" #> SHtml.submit("Utwórz", save) &
      "#delete" #> SHtml.submit("Usuń", delete, "onclick" -> "return confirm('Czy na pewno chcesz usunąć wątek')")

  }
}

