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

class PagesSn extends UsersOperations {

  /** formuje menu szkoły wypisując tytuły strony - dodać linki*/
  def submenu() = {
    val departments = PageDepartment.findAll
    val pages = PageHead.findAll
    
    "#pagemenu" #> departments.map(department => { 
                             <h1>{ department.name }</h1> ++
                             <ul>{ pages.filter(page => page.department == department.name).map(page => { <li>
                             <a href={ "/pages?w=p&id=" + page._id.toString }>{ page.title }</a></li> }) }</ul> 
                             }) &
      "#adminmenu" #> <ul>
                        { if (isTeacher) <li><a href="editpage?id=0"><span>Dodaj artykuł</span></a></li> }
                      </ul>
  }

  //decyduje co pokazać
  def switchContent() = {
    var what = S.param("w") openOr "a"
    what match {
      case "a" => showLastNews() //skrót wszystkich wiadomości
      case "w" => showNews() //pełen news o konkretnym id
      case "p" => pageContent() //konkretna strona stała
      case _ => showError()
    }
  }

  //pokazuje konkretną stonę (nie aktualność)
  private def pageContent() = {
    val id = S.param("id") openOr "0"

    PageHead.find(id) match {
      case Some(pageHead) => {
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
      case _ => S.redirectTo("/pages?w=a")   
    }
  }

  private def showError()= "#pagecontent" #> <h1>Nieprawidłowe parametry! Kliknij w odpowiedni  link na stronie</h1>

  
 private def showLastNews() = {
   val (latestNewses,oldNewses) = NewsHead.findAll.splitAt(30)
    "#pagedepartment *" #> Text("Aktualności") &
    "#pagecontent *" #> latestNewses.map( newsHead => {
      val link = "/pages?w=w&id=" + newsHead._id.toString
     <div class="pageshort">
    		  <a href={link}><img src={newsHead.thumbnailLink} class="frame" /></a>
    	<p class="pageinfo"> 
    	<span class="title"><a href={link}>{newsHead.title}</a></span><br/>
    	<span class="fullname">{newsHead.authorName}</span> 
    	<span class="date">{Formater.formatTime(new Date(newsHead._id.getTime()))}</span>
    	</p>
    	<p class="pageintroduction">
    	{Unparsed(newsHead.introduction)} <a href={link}> Czytaj dalej >></a></p>
    	</div> 
    }) &
    "#allnewses" #> oldNewses.map(newsHead => {
      val link = "/pages?w=w&id=" + newsHead._id.toString
      <a href={link}>{newsHead.title}</a>
    })
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
    	  <span class="date">{Formater.formatTime(new Date(newsHead._id.getTime()))}</span>
    	  {if(isOwner(newsHead.authorId)) <span class="edit"><a href={"/editpage/"+newsHead._id.toString}>Edytuj</a></span> 
    	  else <span></span> }
    	  </p>
    	  <p class="pageintroduction">{Unparsed(newsHead.introduction)}</p>
    	  <div class="pagebody">{Unparsed(contentOption.getOrElse(ArticleContent.create).content)}</div>
    	  </div>
      }
      case _ => S.redirectTo("/pages?w=a")
    }
  }
   

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }   
  }

}
