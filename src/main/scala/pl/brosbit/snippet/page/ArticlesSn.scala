/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import java.util.Date
import _root_.scala.xml.{ NodeSeq, Unparsed, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.pl.brosbit.lib._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.JsonAST._
import _root_.net.liftweb.http.{ S }
import Helpers._

class ArticlesSn extends UsersOperations {

//tagi 
  def submenu() = {
      val tag = S.param("tag").getOrElse("")
    ".extraTags" #> NewsTag.findAll.map(newsTag => 
        { if(tag == newsTag.tag)
            <option value={newsTag.tag} selected="selected"> {newsTag.tag + " (" + newsTag.count.toString() + ")"  } </option>
        else 
        <option value={newsTag.tag} > {newsTag.tag + " (" + newsTag.count.toString() + ")"  } </option>})&
     "#adminmenu" #> <span>
                        { if (isTeacher) <a href="editarticle/0"><img  title="Dodaj artykuł" src="/style/images/article.png" /></a> }
                      </span>
  }
  //decyduje co pokazać
  def switchContent() = {
    var what = S.param("w") openOr "a"
    what match {
      case "a" => showLastNews() //skrót wszystkich wiadomości
      case "w" => showNews() //pełen news o konkretnym id
      case "t" => showNewsFromTag() 
      case "s" => showSearchInTitle() 
      case _ => S.redirectTo("/articles?w=a")
    }
  }


  private def showError()= "#pagecontent" #> <h1>Nieprawidłowe parametry! Kliknij w odpowiedni  link na stronie</h1>

  
 private def showLastNews() = {
   val lastNews = NewsHead.findAll.sortBy(n => - n._id.getTime()) //zastąpić natywnym mongo???
   commonNewsShow(lastNews, "Aktualności")
 }

  /** konkretny news */
  def showNews() = {
    val id = S.param("id") openOr "0"
    NewsHead.find(id) match {
      case Some(newsHead) => {
        val contentOption = ArticleContent.find(newsHead.content)
         "#pagedepartment *" #> Text("Aktualności") &
        "#pagecontent" #> <div id="pagecontent">
    	  <img src={newsHead.thumbnailLink}  class="frame" />  <h1>{newsHead.title}</h1> 
    	  <p class="pageinfo"> <span class="fullname">{newsHead.authorName}</span> 
    	  <span class="date">{Formater.formatDate(new Date(newsHead._id.getTime()))}</span>
    	  {if(isOwner(newsHead.authorId)) <span class="edit">
    	  		<a href={"/editarticle/"+newsHead._id.toString}>Edytuj</a></span> 
    	  else <span></span> }
    	  </p>
    	  <p class="pageintroduction">{Unparsed(newsHead.introduction)}</p>
    	  <br/>
    	  <div class="pagebody">{Unparsed(contentOption.getOrElse(ArticleContent.create).content)}</div>
    	  </div>
      }
      case _ => S.redirectTo("/articles?w=a")
    }
  }
  
  def showNewsFromTag() = {
    var tag = S.param("tag").openOr("")
    if(tag == "") S.redirectTo("/articles?w=a")
    else {
      val newsHeads = NewsHead.findAll(("tags", tag),("$orderby"->("_id"-> -1))) //.sortBy(n => - n._id.getTime()) //sort???
      commonNewsShow(newsHeads, "Aktualności. Oznaczone jako: " + tag)
    }
  }
  
  def showSearchInTitle() = {
    val word = S.param("word").openOr("")
     if(word == "") S.redirectTo("/articles?w=a")
    else {
      //low performers????
      val newsHeads = NewsHead.findAll.sortBy(n => -n._id.getTime()).filter(newsHead => newsHead.title.toLowerCase.contains(word.toLowerCase())).
    		  sortBy(n => - n._id.getTime())
      commonNewsShow(newsHeads, "Aktualności. Szukane słowo: " + word)
    }
  }
  
  private def commonNewsShow(newsHeads:List[NewsHead], info:String) = {
     val (latestNewses,oldNewses) = newsHeads.splitAt(30)
    "#pagecontent *" #> latestNewses.map( newsHead => {
      val link = "/articles?w=w&id=" + newsHead._id.toString
     <div class="pageshort">
    		  <a href={link}><img src={newsHead.thumbnailLink} class="frame" /></a>
    	<p class="pageinfo"> 
    	<span class="title"><a href={link}>{newsHead.title}</a></span><br/>
    	<span class="fullname">{newsHead.authorName}</span> 
    	<span class="date">{Formater.formatDate(new Date(newsHead._id.getTime()))}</span>
    	</p>
    	<p class="pageintroduction">
    	{Unparsed(newsHead.introduction)} </p>
    		<p><a  class="readmore" href={link}> Czytaj dalej >></a></p>
    		<div class="clear"></div>
    	</div> 
    }) &
    "#allnewses" #> oldNewses.map(newsHead => {
      val link = "/articles?w=w&id=" + newsHead._id.toString
      <a href={link}>{newsHead.title}</a>
    })
  }
   

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }   
  }

}
