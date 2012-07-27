/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import scala.xml.{ NodeSeq, Unparsed }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import Helpers._
import java.util.Date
import _root_.net.brosbit4u.lib.Formater

class ForumPostSn extends UsersOperations with UpdateMainPageInfo {

	val id = S.param("id").getOrElse("")
	val threadHead:ForumThreadHead = 
	  ForumThreadHead.find(id) match {
	  case Some(forumThreadHead) => forumThreadHead
	  case _ => S.redirectTo("/forum");ForumThreadHead.create
	}
	val threadContent = ForumThreadContent.find(threadHead.content).getOrElse(ForumThreadContent.create)
	if(threadContent._id != threadHead.content) threadHead.content = threadContent._id

  def departments() = {
    "li" #> ForumDepartment.findAll.map(forumDepart => {
      <li><a href={ "/forum/" + forumDepart._id.toString }>{ forumDepart.name }</a></li>
    })
  }

  def showThread() = {
    
        "h1 *" #> threadHead.department &
        "#threadtitle" #> threadHead.title &
        "#threaddelete" #> {if(isAdmin){<a href={"/forumpost/"+threadHead._id.toString + "?del=1"}>
        						<img src="/style/images/delico.png" style="float:right;" 
        						onclick="return confirm('Czy na pewno usunąć wątek?');"/></a>} else <a></a>} &
        ".firstcomment *" #> <td><div>{Unparsed(threadContent.content)}</div><hr/>
        						<p>#0<span class="fullname">{threadHead.authorName}</span>
        						<span class="date">{Formater.formatDate(new Date(threadHead._id.getTime()))}</span>
        						</p></td>  &
        ".comments" #> threadContent.comments.map(comment => {
        	"div *" #> Unparsed(comment.content) & 
        	"p" #> <p><span>{"#" + comment.id.toString}</span>
        			  <span class="fullname">{comment.authorName}</span>
        			 <span class="date">{comment.date}</span>
        			 </p> &
          "a" #> {if(isAdmin){<a href={"/forumpost/" + threadHead._id.toString + "?clr=" + comment.id.toString}>
                    <img src="/style/images/delico.png" style="float:right;" 
          			onclick="return confirm('Czy na pewno usunąć komentarz?');"/>
          				</a>} else <a></a> }
        })
        
  }
    
  //formatka dodania wpisu do wątku
  def addComment(): CssSel = {
    if (!isLoged) return "fieldset" #> <h3>Zaloguj się aby odpowiedzieć  (login i hasło uzyskasz w szkole)</h3>
    var content = ""

    def save() {
      if(isLoged){
        val user = User.currentUser.get
        val newId = threadContent.comments match {
          case Nil => 1
          case list => list.last.id + 1
        }
        val comment = Comment(newId, user.getFullName, 
        		user.id, Formater.formatTime(new Date()), content)
        threadContent.comments = threadContent.comments :+ comment

      threadHead.count = threadContent.comments.length
      threadHead.lastInfo = threadContent.getLastInfo
      updateForumInfo(comment.date, threadHead.title, threadHead._id.toString)
      threadContent.save
      threadHead.save
      S.redirectTo("/forumpost/"+ threadHead._id.toString)
      }
    }
    
    "#id" #> SHtml.text(threadHead._id.toString, x => x, "type" -> "hidden") &
    "#content" #> SHtml.textarea(content, x => { println(content); content = x.trim }) &   
    "#save" #> SHtml.submit("Dodaj!", save)
  }
  
  
  def deleteCommentOrThread() = {
    if(isAdmin){
       val deleteThread = S.param("del").openOr("")
       val clearComment = tryo(S.param("clr").openOr("").toInt).openOr(0)
       if(deleteThread == "1"){
         println("delete thread!!!!!!!!!!")
         threadContent.delete
         threadHead.delete
         S.redirectTo("/forum")
       }
       if(clearComment > 0){
         threadContent.comments = threadContent.comments.map(comment => {
           if(comment.id == clearComment){
             Comment(comment.id, comment.authorName, comment.authorId, comment.date, 
                 "<strong>Treść niezgodna z regulaminem została usunięta przez administratora</strong>")
           } 
           else comment
         })
         threadContent.save
         S.redirectTo("/forumpost/" + threadHead._id.toString)
       }
    }
    "#deletecommentorthread" #> <span></span>
  }
  
  //experimental - for replace save in addComment
  def updateComments(comment:Comment){
    
  }

}
