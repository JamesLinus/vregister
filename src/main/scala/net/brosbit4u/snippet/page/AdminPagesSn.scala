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

    import _root_.scala.xml.{NodeSeq, Text}
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import _root_.net.liftweb.http.{S, SHtml, RequestVar}
    import _root_.net.liftweb.mapper.{Ascending,OrderBy,By}
    import net.liftweb.http.js._
    import JsCmds._
    import JE._
    import Helpers._

    class AdminPagesSn {

       object notice extends RequestVar[String]("")
     ///!!!!!!dodać wszędzie sprawdzanie czy to admin

      //zrobić walidację maila

      def addDepartment() = {
//      if (!isAdmin_?) return <h1>Nie masz uprawnień do edycji!</h1>
        var id = ""
        var sName = ""
        var fName = ""
    
        def addDep():Unit = {
          var inInt = 0
          id = id.trim
          var dep = Department.create
          try {
            if (id.length > 0) inInt = id.toInt
          } catch {case _ => return }
          if (inInt > 0) {
            dep =  Department.find(id).get
          }
          dep.shortName(sName).longName(fName)
          dep.save
        }
    
        def delDep():Unit = {
          var inInt = 0
          id = id.trim
          try {
            if (id.length > 0) inInt = id.toInt
          } catch {case _ => return }
          if (inInt > 0) {
            val dep =  Department.find(id).get
            val pages = Page.findAll(By(Page.department, dep))
            if (dep.db_can_delete_?) {
              dep.delete_!
              for(p <- pages ) p.delete_!
            }
          }
        }
   

             "#id" #> SHtml.text(id, x => id = x,"size"->"6","readonly"->"readonly","id"->"id") &
             "#shortname" #> SHtml.text(sName, x => sName = x,"size"->"20","maxlength"->"20","id"->"shortname") &
             "#fullname" #> SHtml.text(fName, x => fName = x,"maxlength"->"120","id"->"fullname") &
             "#submitadd" #> SHtml.submit("Zapisz!",addDep, "onclick" -> "return validateForm()") &
             "#submitdel" #> SHtml.submit("Usuń!",delDep,"onclick"->"return confirm('Na pewno chcesz usunąć dział i wszystkie strony działu?');")
      }

      def departments(n:NodeSeq):NodeSeq = {
        val dep = Department.findAll(OrderBy(Department.shortName, Ascending ))
        var node:NodeSeq = <tbody>{
            for(d <- dep)  yield { <tr onclick={"setData(this)"}><td>{d.id.is.toString}</td><td>{d.shortName.is}</td><td>{d.longName.is}</td>
                                   </tr>}
          }</tbody>
        node
      }

      ////////////////////////////////////////////////////////////////////
      /** dodawanie działu do forum*/
      def addForumDep() = {
        //if (!isAdmin_?) return <h1>Nie masz uprawnień do edycji!</h1>
        var id = ""
        var name = ""
    
        def addDep():Unit = {
          var inInt = 0
          id = id.trim
          var dep = ForumDep.create
          try {
            if (id.length > 0) inInt = id.toInt
          } catch {case _ => return }
          if (inInt > 0) {
            dep =  ForumDep.find(id).get
          }
          dep.name(name)
          dep.save
        }

        def delDep():Unit = {
          var inInt = 0
          id = id.trim
          try {
            if (id.length > 0) inInt = id.toInt
          } catch {case _ => return }
          if (inInt > 0) {
            val dep =  ForumDep.find(id).get
            if (dep.db_can_delete_?) dep.delete_!
          }
        }

             "#id" #> SHtml.text(id,x => id = x,"size"->"6","readonly"->"readonly","id"->"id") &
             "#name" #> SHtml.text(name,x => name = x,"size"->"40","maxlength"->"40","id"->"name") &
             "#submitadd" #> SHtml.submit("Zapisz!",addDep, "onclick" -> "return validateForm()") &
             "#submitdel" #> SHtml.submit("Usuń!",delDep,"onclick"->"return confirm('Na pewno chcesz usunąć dział i wszystkie wątki w dziale?');")
     
      }

      def forumDepartments(node:NodeSeq):NodeSeq = {
        val dep = ForumDep.findAll(OrderBy(ForumDep.name, Ascending ))
        var n:NodeSeq = <tbody>{
            for(d <- dep)  yield { <tr onclick={"setData(this)"}><td>{d.id.is.toString}</td><td>{d.name.is}</td>
                                   </tr>}
          }</tbody>
        n
      }

      //////////////////////////////////////////////////////////////////////
      //dodawanie adresów kontakowych mail do wysyłania maili ze strony
      def addContactMail() = {
        var idLong = 0L
        var id = ""
        var mail = ""
        var descript = ""

        def addMail() {
          if (id == "" ) {
            val cm = ContactMail.create
            cm.descript(descript).mail(mail)
            if (cm.save) {
              notice("Adres został zapisany")
            }
            else notice("Adres niezapiany. Sprawdź poprawność adresu e-mail")
          }
          else {
            idLong = id.toLong
            val cm = ContactMail.find(idLong).get
            cm.descript(descript).mail(mail)
            if (cm.save) {
              notice("Adres został zapisany")
            }
            else notice("Adres niezapisany. Sprawdź poprawność adresu e-mail")
          }
        }

        def delMail() {
          val cm = ContactMail.find(id).get
          if (cm.db_can_delete_?) {
            cm.delete_!
            notice("Usunięto adres")
          }
          else notice("Nie można usunąć!")
        }

        
             "#id" #> SHtml.text(id,x => id = x, "size" -> "6", "readonly" -> "readonly", "id"-> "id" ) &
             "#descript" #> SHtml.text(descript, x => descript = x, "size"->"40","maxlength"->"40","id"->"descript") &
             "#mail" #> SHtml.text(mail, x => mail = x, "size"->"40","maxlength"->"40","id"->"mail") &
             "#submitadd" #> SHtml.submit("Zapisz!", addMail, "onclick" -> "return validateForm()") &
             "#submitdel" #> SHtml.submit("Usuń!", delMail,"onclick"->"return confirm('Na pewno chcesz usunąć email?');") &
             "#notice" #> Text(notice)
        
      }
      
      //wyświetlanie listy adresów kontaktowych szkoły
      def contactMails(node:NodeSeq):NodeSeq = {
        val cms = ContactMail.findAll()
        var n:NodeSeq = <tbody>{
            for(c <- cms)  yield { <tr onclick={"setData(this)"}><td>{c.id.is.toString}</td><td>{c.mail.is}</td><td>{c.descript.is}</td></tr>}
          }</tbody>
        n
      }
      
      //zmiana hasła admina
      def changePassword()= {
        var password1 = ""
        var password2 = ""
        var notice = ""
        def changePass() {
          if (password1 == "" || password1 != password2) return
          if(isAdmin_?) { if (User.currentUser.get.password(password1).save) notice ="Hasło zapisano" else notice = "Nieudany zapis hasła"}
        }
        
        "#password1" #> SHtml.text(password1,x => password1 = x.trim, "type"-> "password","id"-> "pass1") &
        "#password2" #> SHtml.text(password2,x => password2 = x.trim, "type"-> "password","id"-> "pass2") &
        "#submit" #> SHtml.submit("Zmień!", changePass, "onclick"->"return checkPass();") &
        "#notice" #> Text(notice)
      }

      def isAdmin_?():Boolean = {
        User.currentUser match {
          case Full(user) => if (user.role.is == "a") true else false
          case _ => false
        }
      }
    }



  }} //package end
