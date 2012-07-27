/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import _root_.scala.xml.{ NodeSeq, Unparsed}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._
import _root_.net.brosbit4u.lib.{ Formater }
import java.util.Date
import _root_.net.liftweb.json.JsonDSL._

class ForumSn extends UsersOperations {
  
  val forumDepartment = getForumDepartment() 
   
  //wyświetlanie nazwy działów z linkami
  def departments() = {
    "li" #> ForumDepartment.findAll.map(forumDepart => {
      <li><a href={ "/forum/" + forumDepart._id.toString }>{ forumDepart.name }</a></li>
    })
  }

  //zawartość strony z listą wątków w wybranym dziale
  def showThreadsTable() = {
    val threads = ForumThreadHead.findAll(("department" -> forumDepartment.name))

    "h1" #> <h1>{forumDepartment.name}</h1> &
      "#tbodytr" #> threads.map(thread => {
        <tr>
          <td><a href={ "/forumpost/" + thread._id.toString }>{ thread.title }</a>
          <br/> <span class="fullname">{ thread.authorName }</span>
          <span class="date">{ Formater.formatTime(new Date(thread._id.getTime())) }</span></td>
          <td>{ thread.count.toString }</td>
          <td>{ Unparsed(thread.lastInfo) }</td>
        </tr>
      })
  }
  
  def linkAddNewThread() = {
    "#linkaddnewthread" #> (if(isLoged) 
    <a href={"/editthread?dep=" + forumDepartment._id.toString}> Dodaj nowy wątek </a>
    else <a href="/user_mgt/login">Zaloguj się aby móc dodawać wątki</a>)
  }

  
  private def getForumDepartment():ForumDepartment = {
     val idFromUrl =  S.param("id").openOr("")
     ForumDepartment.find(idFromUrl) match {
      case Some(forumDepartment) => forumDepartment
      case _ => ForumDepartment.findAll match {
        case Nil => ForumDepartment.create
        case list => list.head
      }
    }
  }

}

