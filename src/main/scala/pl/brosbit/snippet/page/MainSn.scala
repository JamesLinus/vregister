

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
        val allTag = <a href="/index/a">Wszystkie</a>
        val newsTags = allTag::NewsTag.findAll.map(tag => <a href={"/index/t?tag=" + tag.tag}>{ tag.tag + " (" + tag.count.toString() + ")" }</a>).toList
        val menuNews = Map("Aktualności" ->  newsTags )
        val pages = ArticleHead.findAll("news" -> false)
 
        val menuDep = PageDepartment.findAll.map(pageDep => {
        	(pageDep.name,  { val ns:NodeSeq =  pages.filter(p => p.department == pageDep._id).map(page =>
                	<a href={"/index?b=" + page._id}>{ page.title}</a> ).toSeq
                	ns
            })
        }).toMap
        val menu = menuNews ++  menuDep
        
        "#addArticleMenu" #>  <span>
                                { if (isTeacher) <a href="/editarticle/0"><img title="Dodaj artykuł" src="/style/images/article.png"/> Dodaj artykuł</a> }
                            </span> &
         "#accordion"  #>  <div id="accordion">{menu.map(m => { 
            <h3>{ m._1} </h3>
            <ul>{ m._2.map(ahref =>  <li> {ahref}</li>)}</ul>
              })} </div>
           
    }

                                
                                
    def switchContent() = {
        var what = S.param("w") openOr "a"
        what match {
            case "a" => {
                val newses = ArticleHead.findAll(("news" -> true), ("_id" -> -1))
                showNewses(newses, "")
            }
            case "t" => {
                var tag = S.param("tag").openOr("")
                if (tag == "") S.redirectTo("/index")
                else {
                    val newsHeads = ArticleHead.findAll(("news" -> true)~("tags", tag), ("_id" -> -1)) 
                    showNewses(newsHeads, "tag")
                }
            }
            case "b" => {
                val id = S.param("i").openOr("")
                if(id == "") S.redirectTo("/index")
                else {
                    pageContent(id)
                }
            }
            case _ => S.redirectTo("/index")
        }
    }


    def showNewses(newses: List[ArticleHead], tag:String) = {
        val sizeP = 30
        var page = S.param("p").getOrElse("1")
        val pageInt = tryo(page.toInt).getOrElse(1)
        val pages = newses.size / 30 + (if(newses.size % 30 > 0) 1 else 0)
        
        val endNews = if(sizeP*pageInt > newses.size) newses.size else sizeP*pageInt
        val beginNews = if(endNews - 30 < 0) 0 else endNews - 30
        
        val toShowNewses = newses.slice(beginNews,  endNews)
        val choiceContent = if(tag == "") "a?p=" else "t/?tag=" + tag + "&p="
        ".newsInfo" #> <div>  { toShowNewses.map(news => createPinBox(news)) }    </div> &
            ".linkNews" #>  (1 to pages).map(p => {
                <li>{ if(p == pageInt) <span>{p.toString}</span> else <a href={"/index/" + choiceContent + p.toString} >{ p.toString }</a>} </li>
            })

    }

    def lastForumPosts() = {
        val lastForumList = mainPageNewInfo.forum
        "li" #> lastForumList.map(post => {
            <li><a href={ post.link }>{ post.title }</a> </li>
        })
    }

    def showOneNews() = {
        def create(id: String) = {
            ArticleHead.find(id) match {
                case Some(newsHead) => {
                    val contentOption = ArticleContent.find(newsHead.content)
                    <div id="pagecontent">
                    		<span class="closeNewsButton" onclick="closeNews()"><img src="/style/images/delico.png"  /> Zamknij</span>
                    <h1>{ newsHead.title }</h1>
                        <p class="pageinfo">
                            <span class="fullname">{ newsHead.authorName }</span>
                            <span class="date">{ Formater.formatDate(new Date(newsHead._id.getTime())) }</span>
                            {
                                if (isOwner(newsHead.authorId)) <span class="edit">
                                                                    <a href={ "/editarticle/" + newsHead._id.toString }>Edytuj</a>
                                                                </span>
                                else <span></span>
                            }
                        </p>
                        <div class="pagebody">{ Unparsed(contentOption.getOrElse(ArticleContent.create).content) }</div>
                    </div>
                }
                case _ => <div>Błąd - brak wybranej lekcji</div>
            }
        }
        "#hiddenAjaxText" #> SHtml.ajaxText("", id => SetHtml("ajaxNews", create(id)))
    }

    private def isOwner(idFromArticle: Long): Boolean = {
        User.currentUser match {
            case Full(user) => (idFromArticle == user.id.is || user.role.is == "a")
            case _ => false
        }
    }

    private def createPinBox(news: ArticleHead) = {
        <div class="pine-box new-bullet ">
    	<h2 onclick={ "return showNews('" + news._id + "')" }>{ news.title }</h2>
            <div class="imgBox">
                <img src={ news.thumbnailLink }/>
            </div>
            <div class="textBox">
                <p class="pageinfo">
                    <span class="fullname">{ news.authorName }</span>
                    <span class="date">{ Formater.formatDate(new Date(news._id.getTime())) }</span>
                </p>
                <div>{ Unparsed(news.introduction) }</div>
                <span class="readMore" onclick={ "return showNews('" + news._id + "')" }>Czytaj dalej</span>
            </div>
            <div style="clear:both;"></div>
        </div>
    }
    
     private def pageContent(id:String) = {
    val pageHead = ArticleHead.find(id) match {
      case Some(pageHead) => pageHead
      case _ => {
          val ph = ArticleHead.create
          ph.title = "BŁĄD - brak strony"
          ph    
      }
    }
    
     val contentOption = ArticleContent.find(pageHead.content)
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

}
