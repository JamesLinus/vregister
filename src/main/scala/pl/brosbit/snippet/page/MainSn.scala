/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
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
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.JsonAST._
import com.mongodb.DBObject
import http.js.JsCmds.SetHtml
import net.liftweb.json.JsonAST
//import com.mongodb.QueryBuilder

class MainSn extends UsersOperations {

    val mainPageNewInfo = MainPageData.getMainPageNewInfo

    def slides = {
        val slideList = mainPageNewInfo.slides
        "img" #> slideList.map(slide => {
            <a href={ slide.link }><img src={ slide.src } title={ slide.title } alt=""/></a>

        })
    }

    def links = {
        var mainPageLinks = MainPageLinks.findAll match {
            case head :: list => head
            case _ => MainPageLinks.create
        }
        ".grid_3" #> mainPageLinks.links.map(linkGroup => {
            <div class="grid_3">
                <h1>{ linkGroup.name }</h1>
                <ul>
                    { linkGroup.links.map(link => <li><a href={ link.url }>{ link.title }</a></li>) }
                </ul>
            </div>
        })
    }

    def logInfo() = super.loginInfo

    def submenuArticles() = {
        val tag = S.param("tag").getOrElse("")
        ".extraTags" #> NewsTag.findAll.map(newsTag =>
            {
                if (tag == newsTag.tag)
                    <option value={ newsTag.tag } selected="selected"> { newsTag.tag + " (" + newsTag.count.toString() + ")" } </option>
                else
                    <option value={ newsTag.tag }> { newsTag.tag + " (" + newsTag.count.toString() + ")" } </option>
            }) &
            "#adminmenu" #> <span>
                                { if (isTeacher) <a href="editarticle/0"><img title="Dodaj artykuł" src="/style/images/article.png"/></a> }
                            </span>
    }

    def switchContent() = {
        var what = S.param("w") openOr "a"
        what match {
            case "a" => {
                val newses = NewsHead.findAll(("anounce" -> false), ("_id" -> -1))
                showNewses(newses)
            }
            case "t" => {
                 var tag = S.param("tag").openOr("")
                 if(tag == "") S.redirectTo("/index")
                 else {
                		 val newsHeads = NewsHead.findAll(("tags", tag)~("anounce" -> false),("_id"-> -1)) //.sortBy(n => - n._id.getTime()) //sort???
                		 showNewses(newsHeads)
                 }
            }
            case "s" => {
                   val word = S.param("word").openOr("")
                   if(word == "") S.redirectTo("/index")
                   else {
                	   //low performers????
                	   val newsHeads = NewsHead.findAll(("anounce" -> false), ("_id" -> -1)).filter(newsHead => newsHead.title.toLowerCase.contains(word.toLowerCase())).
                			   sortBy(n => - n._id.getTime())
                			   showNewses(newsHeads)
                   }
            }
            case _ => S.redirectTo("/index")
        }
    }


    def showNewses(newses: List[NewsHead]) = {
        var i = -1
        val anounces = NewsHead.findAll(("anounce" -> true), ("_id" -> -1))
        val (latestNewses, oldNewses) = (anounces ::: newses).splitAt(30)
        val newsGroups = latestNewses.map(n => { i += 1; (i % 3, n) }).groupBy(n => n._1)
        val newsList1: List[NewsHead] = if (newsGroups.contains(0)) newsGroups(0).map(g => g._2) else Nil
        val newsList2: List[NewsHead] = if (newsGroups.contains(1)) newsGroups(1).map(g => g._2) else Nil
        val newsList3: List[NewsHead] = if (newsGroups.contains(2)) newsGroups(2).map(g => g._2) else Nil

        ".newsInfo" #> <div>
                           <div id="column1" class="grid_4">
                               {
                                   newsList1.map(news => createPinBox(news))
                               }
                           </div>
                           <div id="column2" class="grid_4">
                               {
                                   newsList2.map(news =>  createPinBox(news))
                               }
                           </div>
                           <div id="column3" class="grid_4">
                               {
                                   newsList3.map(news =>  createPinBox(news))
                               }
                           </div>
                       </div> &
            ".linkNews" #> oldNewses.map(newsHead => {
               <span class="linkNews">|  <span  class="linkNewsA"  href="" onclick={"showNews('"+newsHead._id+"')"}>{ newsHead.title }</span>  |</span>
            })

    }

    def lastForumPosts() = {
        val lastForumList = mainPageNewInfo.forum

        "li" #> lastForumList.map(post => {
            <li><a href={ post.link }>{ post.title }</a> </li>
        })
    }
    
    
    def showOneNews() = {
      def create(id:String) = {
          NewsHead.find(id) match {
              case Some(newsHead) =>  {
        val contentOption = NewsContent.find(newsHead.content)
        <div id="pagecontent">
    	  <img src={newsHead.thumbnailLink}  class="frame" />  <h1>{newsHead.title}</h1> 
    	  <p class="pageinfo"> <span class="fullname">{newsHead.authorName}</span> 
    	  <span class="date">{Formater.formatDate(new Date(newsHead._id.getTime()))}</span>
    	  {if(isOwner(newsHead.authorId)) <span class="edit">
    	  		<a href={"/editarticle/"+newsHead._id.toString}>Edytuj</a></span> 
    	  else <span></span> }
    	  </p>
    	  <p class="pageintroduction">{Unparsed(newsHead.introduction)}</p>
    	  <br/>
    	  <div class="pagebody">{Unparsed(contentOption.getOrElse(NewsContent.create).content)}</div>
    	  </div>
      }
              case _ => <div>Błąd - brak wybranej lekcji</div>  
      }
      }
      "#hiddenAjaxText" #> SHtml.ajaxText("",  id => SetHtml("ajaxNews", create(id)))          
  }
  
    private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
      case _ => false
    }   
  }
    
    private def createPinBox(news:NewsHead) = {
         <div class="pine-box new-bullet ">
                                           <h2 onclick={"return showNews('"+news._id+"')"}>{ news.title }</h2>
                                           <p class="imgBox"><img src={ news.thumbnailLink }/></p>
                                           <p class="pageinfo">
                                               <span class="fullname">{ news.authorName }</span>
                                               <span class="date">{ Formater.formatDate(new Date(news._id.getTime())) }</span>
                                           </p>
                                           <div>{ Unparsed(news.introduction) }</div>
                                          <span class="readMore" onclick={"return showNews('"+news._id+"')"}>Czytaj dalej</span>
                                       </div>
    }

}
