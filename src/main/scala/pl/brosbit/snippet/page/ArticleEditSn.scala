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

class ArticleEditSn {

  def editPage() = {
    val departmentTuples = ("Aktualności","Aktualności")::PageDepartment.findAll.map(department => {
      (department.name, department.name)
    })
    
    var id = S.param("id").openOr("")
    var title = ""
    var authorId = 0L
    var tags:List[String] = Nil
    var thumbnailLink = ""
    var introduction = ""
    var content = ""
      
    if(id.length > 11) NewsHead.find(id)  match {
      case Some(newsHead) => {
        val articleContent = ArticleContent.find(newsHead.content).getOrElse(ArticleContent.create)
        title = newsHead.title
        tags = newsHead.tags
        authorId = newsHead.authorId
        thumbnailLink = newsHead.thumbnailLink
        introduction = newsHead.introduction
        content = articleContent.content
      }
      case _ => 
    }
    
    def save() {
      var isNew = false
      val newsHead = NewsHead.find(id).getOrElse(NewsHead.create)
      if(newsHead.authorId == 0L || isOwner(newsHead.authorId)){
        newsHead.title = title
        val newTags = tags.take(3)
        val toAddTags = getToAddTagsByCompare(newTags, newsHead.tags)
        val toDeleteTags = getToDeleteTagsByCompare(newTags, newsHead.tags)
        newsHead.tags = newTags
        updateNewsTags(toAddTags, toDeleteTags)
        newsHead.introduction = introduction
        //println("Save news with origin link: %s, and new %s".format(newsHead.thumbnailLink,thumbnailLink))
        newsHead.thumbnailLink = if(thumbnailLink == "" ) "/style/images/nothumb.png"
        						 else thumbnailLink
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
      S.redirectTo("/articles?w=w&id=" + newsHead._id.toString)
    }
    

    def discard() {
      S.redirectTo("/articles" )
    }
    
    def delete(){
     {if(id.length > 11) if(isOwner(authorId))  deleteObjectById(id)}
     S.redirectTo("/articles" )
    }
    
     val tagsList =  NewsTag.findAll.map(newsTag => (newsTag.tag, newsTag.tag))
    
    "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    "#title" #> SHtml.text(title, in => title = in.trim) &
    "#tags" #> SHtml.multiSelect(tagsList, tags , tags = _) &
    "#thumbnail" #> SHtml.text(thumbnailLink , in => thumbnailLink = in.trim, "style" -> "display:none;") &
    "#introduction" #> SHtml.textarea(introduction, in => introduction  = deleteBR(in.trim)) &
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
        deleteNewsInfoOnMainPage(newsHead._id.toString)
        this.updateNewsTags(Nil, newsHead.tags)
        newsHead.delete
      }
      case _ => 
    }
  }
  
 private def addNewsInfoOnMainPage(id:String, title:String){
    println("begin add news info")
    val fullLink = getFullLink(id)
    //MainPageData.delete(("link" -> fullLink))
    val mainPageData = MainPageData.create
    println("Keys news to String: " + Keys.news.toString)
    mainPageData.key = Keys.news.toString
    println("MainPageData.key: " + mainPageData.key)
    mainPageData.title = title
    mainPageData.link = fullLink
    mainPageData.save
  }
  
  private def deleteNewsInfoOnMainPage(id:String){
    val fullLink = getFullLink(id)
    MainPageData.delete(("key" -> Keys.news.toString) ~ ("link" -> fullLink))
  }
  
  private def deleteBR(htmlText:String) = htmlText.replaceAll("<br>"," ").replaceAll("<br/>", " ")

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }
  }
  
    private def getFullLink(id:String) = "/articles?w=w&id=" + id
    
    private def getToAddTagsByCompare(newTags:List[String], oldTags:List[String]):List[String] = {
      var toAddTags:List[String] = newTags
      for(tag <- oldTags) toAddTags = toAddTags.filter(_ != tag)
      toAddTags
    }
    
    private def getToDeleteTagsByCompare(newTags:List[String], oldTags:List[String]):List[String] = {
      var toDeleteTags:List[String] = oldTags
      for(tag <- newTags) toDeleteTags = toDeleteTags.filter(_ != tag)
      toDeleteTags
    }
    
    private def updateNewsTags(toAddTags:List[String], toDeleteTags:List[String]){
      val increase = ("$inc" -> ("count" -> 1))
      val decrease = ("$inc" -> ("count" -> -1))
      toAddTags.foreach(tag => NewsTag.update(("tag" -> tag), increase))
      toDeleteTags.foreach(tag => NewsTag.update(("tag" -> tag), decrease))
    }

}


