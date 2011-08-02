/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u {
  package snippet {

import _root_.scala.xml.{NodeSeq}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.brosbit4u.model._
import _root_.net.liftweb.http.{S,SHtml,RequestVar}
import Helpers._



class EditSn {

//  val pageEditLink = <a href="/extra/editpage">Powrót do Edycji</a>
//  val postEditLink = <a href="/extra/edit.html">Powrót do Edycji</a>
//  val hydeparkEditLink = <a href="/extra/edithydepark.html">Powrót do edycji</a>

//  tworzenie nowego postu gdy rozpoczynamy nowy a nie edytujemy
//def createNewPost():String = {
//  val p = Post.create
//  p.confirmed(false).save
//  p.id.toString
//}

object idPost extends RequestVar[Long](
   try { (S.param("id").openOr("0")).toLong }
   catch { case _ => 0L })
object body extends RequestVar[String]("")
object title extends RequestVar[String]("")
object shortTitle extends RequestVar[String]("")
object depId extends RequestVar[Long](0L)

def addPage() = {
 
 var defltOption = ""
  val depart:List[(String,String)] = ("0","wybierz")::Department.findAll().map(d => (d.id.is.toString, d.shortName.is) )
  var id = "0" //id do zapisu do inputa
  if (idPost.get != 0L) {
    id = idPost.get.toString
    val pageB = Page.find(id)
    if (!pageB.isEmpty) {
      val page = pageB.get
      body(page.body.is)
      title(page.title.is)
      shortTitle(page.shortTitle.is)
      depId(page.department.obj.get.id.is)
      defltOption = page.department.obj.get.id.is.toString
    }
  }

//zapis postu
  def savePage() {
    var page = id match {
      case "0" => Page.create
      case id => Page.find(id) match {
               case Full(p) => p
               case _ => throw new IllegalAccessException("Nieprawidłowe id")
              }
    }
    if ( isAdmin_?) page.shortTitle(shortTitle.get).title(title.get).body(body.get).department(Department.find(depId)).save
    S.redirectTo("/index?w=p&id=" + id)
  }
//usunięcie
  def delPage() { 
    Page.find(id) match {
      case Full(p) if (isAdmin_?) => p.delete_!
      case _ => Unit
    }
    S.redirectTo("/index")
  }

  def disPage() {
    //S.redirectTo("/index?w=p&id=" + id)
  }


  //akcja zapisu pliku --  do poprawy
  
  if (isTeacher_?) {
       "#id" #> SHtml.text(id,x =>  id = x,"type"-> "hidden") &
       "#title" #> SHtml.text(title.is, x => title(x),"maxlength"->"120","id"-> "title") &
       "#shorttitle" #> SHtml.text(shortTitle.is, x => shortTitle(x),"maxlength"->"25","id" -> "short") &
       "#departs" #> SHtml.select(depart,if (defltOption.isEmpty) Empty else Full(defltOption),s => depId(s.toLong)) &
       "#editor" #> SHtml.textarea(body.is, x => body(x),"class" ->"wymeditor", "style" -> "width:800px; height:600px","id" -> "wymeditor") &
       "save" #> SHtml.submit("Zapisz!", savePage,"class" -> "wymupdate","onclick"->"return isValid();") &
       "delete" #> SHtml.submit("Usuń!", delPage,"onclick" -> "return confirm('Na pewno chcesz usunąć?');") &
       "discard" #> SHtml.submit("Anuluj!",disPage,"onclick"-> "return confirm('Napewno nie zapisywać?');")
  }
  else {
    "#form" #> <h1>Nie masz uprawnień do dodawania postów. Musisz być zalogowany jako nauczyciel.</h1>
  }
}

  //// --------------------------- strona
 def addPost() = {

  var id = "0"
  if (idPost.get != 0L ) {
    id = idPost.get.toString
    val postB = Post.find(id)
    if (!postB.isEmpty) {
      val post = postB.get
      body(post.body.is)
      title(post.title.is)
    }
  }
  def savePost() {
    var post = id match {
      case "0" => Post.create
      case id => Post.find(id) match {
               case Full(p) => p
               case _ => throw new IllegalAccessException("Nieprawidłowe id")
              }
    }
    //dodać walidację!
    if ( (id == "0" && isTeacher_?) || (id != "0" && User.currentUser.get.id.is == post.author.obj.get.id.is ))
    post.title(title.get).body(body.get).author(User.currentUser).save
    else  post.title(title.get).body(body.get).save
    
    S.redirectTo("/index?w=w&id=" + post.id.is.toString)

  }

  def delPost() {
    Post.find(id) match {
      case Full(p) => p.delete_!
      case _ => Unit
    }
    S.redirectTo("/index")
  }

  def disPost() {
    S.redirectTo("/index")
  }

        //zmienić na nauczyciela, właściciela wpisu
  if (isOwner_?(id.toLong)) {

       "#id" #> SHtml.text(id,x =>  id = x,"type"-> "hidden") &
       "#title" #> SHtml.text(title, x => title(x),"maxlength"->"120","id"->"title") &
       "#editor" #> SHtml.textarea(body, x => body(x),"class" ->"wymeditor", "style" -> "width:800px; height:500px","id"->"wymeditor") &
       "#save" #> SHtml.submit("Zapisz!", savePost,"class"->"wymupdate","onclick"->"return isValid();") &
       "#delete" #> SHtml.submit("Usuń!", delPost,"onclick"-> "return confirm('Na pewno chcesz usunąć?');") &
       "#discard" #> SHtml.submit("Anuluj!",disPost,"onclick"-> "return confirm('Na pewno nie chcesz zapisać?');")
      
  }
  else {
    "#form" #> <h1>Nie masz uprawnień do dodawania stron. Musisz być zalogowany jako administrator.</h1>
  }
}      
   //////////////// -------------------------------- koniec strony
      
      

      def isAdmin_?():Boolean = {
    User.currentUser match {
      case Full(user) => if (user.role.is == "a") true else false
      case _ => false
    }
   }

      def isTeacher_?():Boolean = {
    User.currentUser match {
      case Full(user) => if (user.role.is == "a" || user.role.is == "n" || user.role.is == "d") true else false
      case _ => false
    }
   }
    // czy obecny użytkownik jest właścicielem posta
     def isOwner_?(id:Long):Boolean = {
       if(id == 0) isTeacher_?
       else {
         val post = Post.find(id)
         if (post != Empty) {
           User.currentUser match {
             case Full(user) => if (post.get.author == user) true else false
             case _ => false
           }
         } else false
       }
     }

   def isUser_?():Boolean = {
    User.currentUser match {
      case Full(user) =>  true
      case _ => false
    }
 }

     
  
}
  }} //package end

