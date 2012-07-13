/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet

import _root_.scala.xml.{ NodeSeq, Unparsed }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._

class EditSn {

  def editPage() = {
    val departmentTuples = ("Aktualności","Aktualności")::PageDepartment.findAll.map(department => {
      (department.name, department.name)
    })
    
    var id = S.param("id").openOr("")
    
    var title = ""
    var authorId = 0L
    var department = "Aktualności"
    var thumbnailLink = ""
    var introduction = ""
    var content = ""
      
    if(id.length > 11) NewsHead.find(id)  match {
      case Some(newsHead) => {
        val articleContent = ArticleContent.find(newsHead.content).getOrElse(ArticleContent.create)
        title = newsHead.title
        authorId = newsHead.authorId
        thumbnailLink = newsHead.thumbnailLink
        introduction = newsHead.introduction
        content = articleContent.content
      }
      case _ => {
        PageHead.find(id) match {
          case Some(pageHead) => {
            val articleContent = ArticleContent.find(pageHead.content).getOrElse(ArticleContent.create)
            title = pageHead.title
            department = pageHead.department
            authorId = pageHead.authorId
            content = articleContent.content
          }
          case _ => 
        }
      } 
    }

    def save() {
        if(department == "Aktualności"){
          saveNews()
        } 
        else {
          savePage()
        }
    }
    
    def saveNews() {
      var isNew = false
      val newsHead = NewsHead.find(id).getOrElse(NewsHead.create)
      if(newsHead.authorId == 0L || isOwner(newsHead.authorId)){
        newsHead.title = title
        newsHead.introduction = introduction
        newsHead.thumbnailLink = thumbnailLink
        val articleContent = ArticleContent.find(newsHead.content).getOrElse(ArticleContent.create)
        articleContent.content = content
        articleContent.save
        newsHead.content = articleContent._id
        if(newsHead.authorId == 0L){
            isNew = true
            val user = User.currentUser.get
            newsHead.authorId = user.id
            newsHead.authorName = user.getFullName
        }
        newsHead.save
        if (isNew) addNewsInfoOnMainPage(newsHead._id.toString, newsHead.title)
      }
      S.redirectTo("/pages?w=a&id=" + newsHead._id.toString)
    }
    
    def savePage(){
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
      S.redirectTo("/pages?w=a&id=" + pageHead._id.toString)
    }

    def discard() {
      S.redirectTo("/pages" )
    }
    
    def delete(){
     {if(id.length > 11) if(isOwner(authorId))  deleteObjectById(id)}
     S.redirectTo("/pages" )
    }
    
    "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    "#title" #> SHtml.text(title, in => title = in.trim) &
    "#department" #> SHtml.select(departmentTuples,Full(department), department = _) &
    "#thumbnail" #> SHtml.text(thumbnailLink , in => thumbnailLink = in.trim, "style" -> "display:none;") &
    "#introduction" #> SHtml.textarea(introduction, in => introduction  = in.trim) &
    "#editor" #> SHtml.textarea(content, in => content = in.trim) &
    "#save" #> SHtml.submit("Zapisz", save, "onclick"->"return isValid()") &
    "#delete" #> SHtml.submit("Usuń", delete,  "onclick"->"return confirm('Na pewno usunąć wpis?');") &
    "#discard" #> SHtml.submit("Porzuć", discard, "onclick"->"return confirm('Na pewno porzucić bez zapisu?');") 
     
  }
  
  private def deleteObjectById(id:String) {
     NewsHead.find(id)  match {
      case Some(newsHead) => {
        val articleContentOpt = ArticleContent.find(newsHead.content)
        if (!articleContentOpt.isEmpty) articleContentOpt.get.delete
        newsHead.delete
      }
      case _ => {
        PageHead.find(id) match {
          case Some(pageHead) => {
            val articleContentOpt = ArticleContent.find(pageHead.content)
            if (!articleContentOpt.isEmpty) articleContentOpt.get.delete
            pageHead.delete
          }
          case _ => 
        }
      }
    }
  }
  
  private def addNewsInfoOnMainPage(id:String, title:String){
    val mainPageNewInfoList = MainPageNewInfo.findAll
    val mainPageNewInfo = if(mainPageNewInfoList.isEmpty) MainPageNewInfo.create 
    					  else mainPageNewInfoList.head
    val newInNews = NewInNews("/pages?w=w&id=" + id, title)
    mainPageNewInfo.news = newInNews::(mainPageNewInfo.news)
    mainPageNewInfo.save
  }

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }
  }

}


