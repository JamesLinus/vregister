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

class PageEditSn {

  def editPage() = {
    val departmentTuples = PageDepartment.findAll.map(department => {
      (department.name, department.name)
    })
    
    var id = S.param("id").openOr("")
    var title = ""
    var authorId = 0L
    var department = "Aktualności"
    var thumbnailLink = ""
    var introduction = ""
    var content = ""
      
    if(id.length > 11) PageHead.find(id) match {
          case Some(pageHead) => {
            val articleContent = ArticleContent.find(pageHead.content).getOrElse(ArticleContent.create)
            title = pageHead.title
            department = pageHead.department
            authorId = pageHead.authorId
            content = articleContent.content
          }
          case _ => 
    }
    
    def save(){
     val pageHead = PageHead.find(id).getOrElse(PageHead.create)
      if(pageHead.authorId == 0L || isOwner(pageHead.authorId)){
          pageHead.title = title
          pageHead.department = department
          val articleContent = ArticleContent.find(pageHead.content).getOrElse(ArticleContent.create)
          articleContent.content = content
          articleContent.save
          pageHead.content = articleContent._id
          if(pageHead.authorId == 0L){
              val user = User.currentUser.get
              pageHead.authorId = user.id
              pageHead.authorName = user.getFullName
          }
         pageHead.save
      }
      S.redirectTo("/bip?w=p&id=" + pageHead._id.toString)
    }

    def discard() {
      S.redirectTo("/bip" )
    }
    
    def delete(){
     {if(id.length > 11) if(isOwner(authorId))  deleteObjectById(id)}
     S.redirectTo("/bip" )
    }
    
    "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    "#title" #> SHtml.text(title, in => title = in.trim) &
    "#department" #> SHtml.select(departmentTuples,Full(department), department = _) &
    "#editor" #> SHtml.textarea(content, in => content = in.trim) &
    "#save" #> SHtml.submit("Zapisz", save, "onclick"->"return isValid()") &
    "#delete" #> SHtml.submit("Usuń", delete,  "onclick"->"return confirm('Na pewno usunąć wpis?');") &
    "#discard" #> SHtml.submit("Porzuć", discard, "onclick"->"return confirm('Na pewno porzucić bez zapisu?');") 
     
  }
  
  private def deleteObjectById(id:String) {
        PageHead.find(id) match {
          case Some(pageHead) => {
            val articleContentOpt = ArticleContent.find(pageHead.content)
            if (!articleContentOpt.isEmpty) articleContentOpt.get.delete
            pageHead.delete
          }
          case _ => 
        }
  }

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }
  }
  
    private def getFullLink(id:String) = "/bip?w=w&id=" + id

}

