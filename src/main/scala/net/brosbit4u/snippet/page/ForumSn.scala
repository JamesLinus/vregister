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

    import _root_.scala.xml.{ NodeSeq }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
    import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
    import Helpers._
    import net.brosbit4u.lib.{ Formater }
    import java.util.Date

    class ForumSn extends UsersOperations {

      object id extends RequestVar[Long](0L)

      //wyświetlanie nazwy działów z linkami
      def departments(node: NodeSeq): NodeSeq = {
        val depart = ForumDep.findAll()
        val n = <ul>
                  <li><a href={ "forum?id=0" }>Najnowsze</a></li>
                  { for (d <- depart) yield { <li><a href={ "forum?id=" + d.id.toString }>{ d.name.is }</a></li> } }
                </ul>
        n
      }

      //zawartość strony z listą wątków w wybranym dziale
      def showThreads(node: NodeSeq): NodeSeq = {
        var idT = 0L
        try {
          idT = S.param("id").getOrElse("0").toLong
        } catch {
          case _ => return <h3>Nieprawidłowy link. Kliknij poprawny link</h3>
        }
        if (idT > 0L) id(idT)
        //usuwanie postu przez admina
        else if (idT == -1L && isAdmin) {
          val idToDel = S.param("t").openOr("0")
          var idLong = 0L
          try {
            idLong = idToDel.toLong
          } catch {
            case _ => idLong = 0L
          }
          if (idLong > 0) {
            val thr = ForumThread.find(idLong)
            if (!thr.isEmpty) {
              thr.get.delete_!
            }
          }
        } //koniec usuwania postu
        if (id.is < 0) {
          S.redirectTo("forum")
          return <h1>Przekierowanie ...</h1>
        }

        val department = ForumDep.find(id.is)
        val threads = id.is match {
          case 0L => ForumThread.findAll(OrderBy(ForumThread.lastTime, Descending)).take(30)
          case idt: Long => ForumThread.findAll(By(ForumThread.department, idt), OrderBy(ForumThread.lastTime, Descending))
        }
        var nr = 0
        val n = <div class="forum">
                  <h2><span>{ if (id.is == 0) "Najnowsze wpisy" else department.get.name.is }</span></h2>
                  <table>
                    <tr class="header1">
                      <td class="col1"></td><td class="col2">Wątek / Autor wątku</td>
                      <td class="col3">Posty:</td><td class="col4">Ostatni post / autor</td>
                    </tr>
                    {
                      for (t <- threads) yield {
                        nr += 1
                        <tr class={ if (nr % 2 == 0) "row1" else "row2" }>
                          <td>{
                            if (isAdmin) <a href={ "/forum?id=-1&t=" + t.id.toString } onclick="return confirm('Na pewno usunąć wątek?');"><img src={ "style/images/delpost.png" }/></a>
                            else <a href={ "/forumpost?id=" + t.id.is.toString }><img src="style/images/post.png"/></a>
                          }</td>
                          <td>
                            <a href={ "/forumpost?id=" + t.id.is.toString }>{ t.title.is }</a>
                            <br/>
                            Autor:{ t.author.obj.get.getFullName }
                          </td>
                          <td>{ t.comments.length.toString }</td><td>{ Formater.formatTime(t.lastTime.is) }<br/>{ t.lastPoster.obj.get.getFullName }</td>
                        </tr>
                      }
                    }
                  </table>
                </div>
        //S.notice("id= " + id.is) //debuging
        n
      }

      //formatka dodania wątku - razem z pierwszym postem
      def newThread(in: NodeSeq): NodeSeq = {
        var title = ""
        var content = ""
        def addThread() {
          //S.notice("in addThread id= " + id.is) //debuging
          if (User.currentUser != Empty) {
            if (title.trim != "" && content.trim != "") {
              val ft = ForumThread.create
              val dep = ForumDep.find(id.is)
              ft.title(title).author(User.currentUser).department(dep).lastPoster(User.currentUser).lastTime(new Date())
              val fc = ForumCom.create.author(User.currentUser).content(content).thread(ft)
              ft.comments += fc
              ft.save
            }
          }
        }

        if (isLoged) {
          if (id.is == 0L) {
            <p class="infoForum">Jeżeli chcesz utworzyć nowy temat wybierz najpierw dział</p>
          } else {
            ("#title" #> SHtml.text(title, x => title = x, "id" -> "titlepost") &
              "#content" #> SHtml.textarea(content, x => content = x, "id" -> "commentwindow") &
              "#idThread" #> SHtml.text(id.is.toString, x => id(x.toLong), "type" -> "hidden") &
              "#add" #> SHtml.submit("Utwórz", addThread, "class" -> "wymupdate", "onclick" -> "isValid();"))(in)
          }

        } else <p class="infoForum">Zaloguj się aby dodać nowy wątek (login i hasło uzyskasz w szkole)</p>

      }

    }

  }
} //ends packages
