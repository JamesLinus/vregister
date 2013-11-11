/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   Licence AGPL v3 see http://www.gnu.org/licenses/
 */

package pl.brosbit.snippet.page

import java.util.Date
import scala.xml.{ NodeSeq, Unparsed, Text }
import _root_.net.liftweb._
import util._
import common._
import mongodb.Limit
import _root_.pl.brosbit._
import model.page._
import model._
import lib._
import _root_.net.liftweb.http.{ S }
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.JsonAST._
import com.mongodb.DBObject

import net.liftweb.json.JsonAST
//import com.mongodb.QueryBuilder

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
  
  
  def lastInfo() = {
      var i = -1
       val anounces = NewsHead.findAll(("anounce"->true),("_id" -> -1))
       val restNews = NewsHead.findAll(("anounce"->false),("_id" -> -1), Limit(27)) 
       val newsGroups = (anounces:::restNews).map(n => {i += 1; (i %3 , n)}).groupBy(n => n._1)
     val newsList1:List[NewsHead] = if(newsGroups.contains(0)) newsGroups(0).map(g => g._2) else Nil
     val newsList2:List[NewsHead] =  if(newsGroups.contains(1)) newsGroups(1).map(g => g._2)else Nil
     val newsList3:List[NewsHead] =  if(newsGroups.contains(2)) newsGroups(2).map(g => g._2) else Nil
 
      ".newsInfo" #> <div>  <div id="column1" class="grid_4">{newsList1.map(news => {
      <div class="pine-box new-bullet ">
       <h2><a href={"/articles?w=w&id="+ news._id}>{news.title}</a></h2>
      <p class="imgBox"><img src={news.thumbnailLink} /></p>  
       <p class="pageinfo"> <span class="fullname">{news.authorName}</span> 
    	  <span class="date">{Formater.formatDate(new Date(news._id.getTime()))}</span>
     </p>
      <div>{Unparsed(news.introduction)}</div>
      <a href={"/articles?w=w&id="+ news._id}>Czytaj dalej</a>
      </div>})  }
      </div>
      <div id="column2" class="grid_4">{newsList2.map(news => {
      <div class="pine-box new-bullet ">
      <h2><a href={"/articles?w=w&id="+ news._id}>{news.title}</a></h2>
      <p class="imgBox"><img src={news.thumbnailLink} /></p>
       <p class="pageinfo"> <span class="fullname">{news.authorName}</span> 
    	  <span class="date">{Formater.formatDate(new Date(news._id.getTime()))}</span>
     </p>
      <div>{Unparsed(news.introduction)}</div>
      <a href={"/articles?w=w&id="+ news._id}>Czytaj dalej</a>
      </div>})  }
      </div>
      <div id="column3" class="grid_4">{newsList3.map(news => {
      <div class="pine-box new-bullet ">
      <h2><a href={"/articles?w=w&id="+ news._id}>{news.title}</a></h2>
      <p class="imgBox"><img src={news.thumbnailLink} /></p>
       <p class="pageinfo"> <span class="fullname">{news.authorName}</span> 
    	  <span class="date">{Formater.formatDate(new Date(news._id.getTime()))}</span>
     </p>
      <div>{Unparsed(news.introduction)}</div>
      <a href={"/articles?w=w&id="+ news._id}>Czytaj dalej</a>
      </div>})  }
      </div>
      </div>

  }
  

  
  def lastForumPosts() = {
    val lastForumList = mainPageNewInfo.forum
    
    "li" #> lastForumList.map(post => {
      <li><a href={post.link}>{post.title}</a> </li>
    })
  }
  

}
