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

    import _root_.scala.xml.{ NodeSeq, Unparsed }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    //import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
    import Helpers._
    import java.util.Date
    import net.brosbit4u.lib.Formater

    class ForumPostSn extends UsersOperations {

      object id extends RequestVar[Long]({
        try {
          S.param("id").getOrElse("0").toLong
        } catch {
          case _ => 0L
        }
      })

      def departments(node: NodeSeq): NodeSeq = {
        val depart = ForumDep.findAll()
        val n = <ul>
                  <li><a href={ "forum?id=0" }>Najnowsze</a></li>
                  { for (d <- depart) yield { <li><a href={ "forum?id=" + d.id.toString }>{ d.name.is }</a></li> } }
                </ul>
        n
      }

      //pokazuje wątek
      def showThread(): NodeSeq = {
        if (id.get == 0) {
          ".threadpots" #> <h1>Nieprawidłowy URL</h1>
        }

        val postB = ForumThread.find(id.is)
        postB match {
          case Empty => {
            S.redirectTo("/forum")
            return <h1>Nieprawidłowy URL</h1>
          }
          case _ => Unit
        }
        val post = postB.get

        val coments: List[ForumCom] = post.comments.toList
        //usuwanie komentarza przez admina
        S.param("dp").getOrElse("0") match {
          case "0" => Unit
          case idCom => {
            ForumCom.find(idCom) match {
              case Full(comment) => {
                if (isAdmin) {
                  comment.delete_!
                  S.redirectTo("/forumpost?id=" + id.toString)
                  return <h1>Komentarz usunięty!S</h1>
                } else S.notice("Nie admin!")
              }
              case _ => S.notice("Brak komentarza: " + idCom); Unit
            }
          }
        }

        var nr = 0
        val n = <div class="forum">
                  <h2><span>{ post.department.obj.get.name }</span></h2>
                  <table>
                    <tr class="header2"><td colspan="4">{ post.title }</td></tr>
                    {
                      for (com <- coments) yield {
                        nr += 1
                        <tr class={ if (nr % 2 == 0) "row1" else "row2" }>
                          <td><div class="comment">
                                { Unparsed(com.content.get) }<br/>
                                _____<br/>
                                <img src="style/images/author_mini.png"/>{ com.author.obj.get.getFullName }<img src="style/images/data_mini.png"/>{ Formater.formatTime(com.createdAt.is) }
                                { if (isAdmin) <a href={ "forumpost?id=" + id.toString + "&dp=" + com.id.is.toString } onclick=" return confirm('Na pewno usunąć post?');"><img src="style/images/delpost.png" style="float:right;"/></a> }
                              </div></td>
                        </tr>
                      }
                    }
                  </table>
                </div>
        n
      }

      //formatka dodania wpisu do wątku
      def newComment(): CssSel = {
        if (!isLoged) return "#forms" #> <h3>Zaloguj się aby odpowiedzieć  (login i hasło uzyskasz w szkole)</h3>
        var content = ""

        def addComment() {
          val post = ForumThread.find(id.is).get
          val coment = ForumCom.create.content(content).thread(post).author(User.currentUser)
          post.comments += coment
          post.lastPoster(User.currentUser).lastTime(new Date()).save
        }
        "#content" #> SHtml.textarea(content, x => { println(content); content = x }, "id" -> "commentwindow") &
          "#idThread" #> SHtml.text(id.is.toString, x => id(x.toLong), "type" -> "hidden") &
          "#add" #> SHtml.submit("Dodaj!", addComment, "class" -> "wymupdate")
      }

    }

  }
} //end of packages
