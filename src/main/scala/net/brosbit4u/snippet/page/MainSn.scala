/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   Licence AGPL v3 see http://www.gnu.org/licenses/
 */

package net.brosbit4u.snippet.page

import scala.xml.{ NodeSeq, Unparsed, Text }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.brosbit4u.lib._
import _root_.net.liftweb.mapper.{ OrderBy, By, Descending, Ascending }
import _root_.net.liftweb.http.{ S }
import Helpers._

class MainSn extends UsersOperations {
  
  val mainPageNewInfo = MainPageData.getMainPageNewInfo

  def slides = {
    val slideList = mainPageNewInfo.slides
    "img" #> slideList.map( slide => {
       <a href={slide.link}><img src={slide.src} title={slide.title} alt="" /></a>
      
    })
  }
  
  def links = {
    var mainPageLinks = MainPageLinks.findAll match {
      case head::list => head
      case _ => MainPageLinks.create
    }
    ".grid_3" #>  mainPageLinks.links.map(linkGroup => {
      <div class="grid_3">
    	<h1>{ linkGroup.name }</h1>
    		<ul>
                { linkGroup.links.map(link => <li><a href={ link.url } >{ link.title }</a></li>)}     
            </ul>
      </div>
    })  
  }
  
  def logInfo() = super.loginInfo
  
  def anounces() = {
    val anouncesList = mainPageNewInfo.anounces
    ".pine-box" #>  anouncesList.map(anounce => {
      <div class="pine-box new-bullets">
      <h2>{anounce.title}</h2>
      <div>{Unparsed(anounce.content)}</div>
      </div>
    })
  }
  
  def lastArticles() = {
    val lastArticlesList = mainPageNewInfo.news
    
    "li" #> lastArticlesList.map(article => {
      <li> <a href={article.link} > {article.title}</a> </li>
    })
  }
  
  def lastForumPosts() = {
    val lastForumList = mainPageNewInfo.forum
    
    "li" #> lastForumList.map(post => {
      <li><a href={post.link}>{post.title}</a> </li>
    })
  }
  

}
