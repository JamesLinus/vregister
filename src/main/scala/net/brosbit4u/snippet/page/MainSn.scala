/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   Licence AGPL v3 see http://www.gnu.org/licenses/
 */

package net.brosbit4u.snippet

import scala.xml.{ NodeSeq, Unparsed, Text }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model._
import _root_.net.brosbit4u.lib._
import _root_.net.liftweb.mapper.{ OrderBy, By, Descending, Ascending }
import _root_.net.liftweb.http.{ S }
import Helpers._

class MainSn extends UsersOperations {
  
  val newInfo = MainPageNewInfo.findAll match {
    case mainPageNewInfo::Nil => mainPageNewInfo
    case _ => MainPageNewInfo.create
  }

  def slides = {
    val slideList = newInfo.slides
    "img" #> slideList.map( slide => {
       <a href={slide.link}><img src={slide.src} title={slide.description} alt="" /></a>
      
    })
  }
  
  def links = {
    val links = newInfo.links
    ".grid_3" #> links.map(linkGroup => {
      <div class="grid_3">
    	<h1>{ linkGroup.department }</h1>
    		<ul>
                { linkGroup.links.map(link => <li><a href={ link.link } >{ link.description }</a></li>)}     
            </ul>
      </div>
    })   
  }
  
  def logInfo() = super.loginInfo
  
  def anounces() = {
    val anouncesList = newInfo.anounces
    ".pine-box" #>  anouncesList.map(anounce => {
      <div class="pine-box new-bullets">
      <h2>{anounce.title}</h2>
      <div>{Unparsed(anounce.content)}</div>
      </div>
    })
  }
  
  def lastArticles() = {
    val lastArticlesList = newInfo.news
    
    "li" #> lastArticlesList.map(article => {
      <li> <a href={article.link} > {article.title}</a> </li>
    })
  }
  
  def lastForumPosts() = {
    val lastForumList = newInfo.forum
    
    "li" #> lastForumList.map(post => {
      <li><a href={post.link}>{post.title}</a> </li>
    })
  }
  

}
