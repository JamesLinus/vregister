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

    import _root_.scala.xml.{ NodeSeq, Unparsed, Text }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import net.brosbit4u.lib._
    import _root_.net.liftweb.mapper.{ OrderBy, By, Descending, Ascending }
    import _root_.net.liftweb.http.{ S }
    import Helpers._

    class SchoolSn extends UsersOperations {

      /** formuje menu szkoły wypisując tytuły strony - dodać linki*/
      def submenu() = {
        var departs = Department.findAll()
        val linksDep = LinkDepartment.findAll()

        "#linksmenu" #> <div>{
          for (linkDep <- linksDep) yield <div>
                                            <h4>{ linkDep.name.is }</h4>
                                            <ul>{ linkDep.links.all.map(link => <li><a href={ link.url.is } target="_blank">{ link.name.is }</a></li>) }</ul>
                                          </div>
        }</div> &
          "#pagemenu" #> <div>{
            for (department <- departs) yield <div>
                                                <h4>{ department.shortName.is }</h4>
                                                <ul>{ department.pages.all.map(page => <li><a href={ "/index?w=p&id=" + page.id.is.toString }>{ page.shortTitle.is }</a></li>) }</ul>
                                              </div>
          }</div> &
          "#adminmenu" #> <ul>
                            { if (isTeacher) <li><a href="editpost?id=0"><span>Dodaj post</span></a></li> }
                            { if (isAdmin) <li><a href="editpage"><span>Dodaj stronę</span></a></li> }
                            { if (isAdmin) <li><a href="admin/pages"><span>Adminstracja</span></a></li> }
                          </ul>
      }

      def insertPages(department: Department): NodeSeq = {
        var pages = Page.findAll(By(Page.department, department))
        var node: NodeSeq = <ul>
                              { for (p <- pages) yield { <li><a href={ "index?w=p&id=" + p.id.toString }>{ p.shortTitle.is }</a></li> } }
                            </ul>
        node
      }

      //decyduje co pokazać
      def switchContent(node: NodeSeq): NodeSeq = {
        var what = S.param("w") openOr "a"
        what match {
          case "a" => showLastNews(node) //skrót wszystkich wiadomości
          case "w" => showNews(node) //pełen news o konkretnym id
          case "p" => pageContent(node) //konkretna strona stała
          case _ => showError(node)
        }
      }

      //pokazuje konkretną stonę (nie aktualność)
      def pageContent(node: NodeSeq): NodeSeq = {
        val id = S.param("id") openOr "0"
        var idInt = 0
        try {
          idInt = id.toInt
        }

        Page.find(id) match {
          case Empty => {
            Page.findAll() match {
              case Nil => <h1>Nieutworzono jeszcze żadnej strony</h1>
              case page :: pages => <div class="page">
                                      <h2><span>{ page.title.is }</span></h2>
                                      { Unparsed(page.body.is) }
                                    </div>
            }
          }
          case Full(page) => <div class="page">
                               <h2><span>{ page.title.is }</span></h2>
                               { Unparsed(page.body.is) }
                               { if (isAdmin) <p id="editlink"><br/><a href={ "editpage?id=" + page.id.toString }>.::Edytuj stronę::.</a></p> }
                             </div>
          case _ => <div class="page">Błąd</div>
        }
      }

      def showError(n: NodeSeq): NodeSeq = {
        <h1>Nieprawidłowe parametry! Kliknij w odpowiedni  link na stronie</h1>
      }

      /** wyświetalnie skrótów ostatnich newsów */
      def showLastNews(node: NodeSeq): NodeSeq = {
        val newsList = Post.findAll(OrderBy(Post.createdAt, Descending)) //.dropRight(25)
        //S.notice("Postów:" + newsList.length.toString) //development
        var n: NodeSeq = <div class="shortnews"><h2><span>Aktualności</span></h2><br/>{
          for (news <- newsList) yield {
            <a href={ "index?w=w&id=" + news.id.toString }><h2>{ news.title }</h2></a>
            <p class="info">
              <img src="style/images/author_mini.png"/>
              { news.author.obj.get.getFullName }
              <img src="style/images/data_mini.png"/>{ Formater.formatTime(news.createdAt.is) }
            </p>
            <br class="spacer"/>
            <p>{ createShortNews(news.body.is) }</p>
            <p class="dotLine"><a href={ "index?w=w&id=" + news.id.toString } title="Czytaj dalej" class="viewMore">Czytaj dalej</a></p>
            <br class="spacer"/>
          }
        }</div>

        return n
      }

      /**
       * skrót newsów z danego działu
       * def showDepartmentNews():NodeSeq = {
       * val dep = S.param("d") openOr "0"
       * var depLong = 0L
       * try {
       * depLong = dep.toLong
       * }
       * val newsList = Post.findAll(By(Post.department,depLong),OrderBy(Post.createdAt,Descending))
       * var n:NodeSeq = <div class="shortnews">
       * <h2><span>{Department.find(depLong).get.longName}</span></h2> {
       * for (news <- newsList) yield {
       * <a href={"index?w=w&id=" + news.id.toString }><h2>{news.title}</h2></a>
       * <p class="info">  <img src="style/images/author_mini.png" />
       * {news.author.obj.get.getFullName }
       * <img src="style/images/data_mini.png" /> {Formater.formatTime(news.createdAt.is)}
       * <img src="style/images/folder_icon_01.png" /> {news.department.obj.get.shortName}</p>
       * <p>{createShortNews(news.body.is)}</p>
       * <p class="dotLine"><a href={"index?w=w&id=" + news.id.toString} title="Czytaj dalej" class="viewMore">Czytaj dalej</a></p>
       * <br class="spacer" />
       * }
       * } </div>
       * n
       * }
       */

      /** konkretny news */
      def showNews(node: NodeSeq): NodeSeq = {
        val id = S.param("id") openOr "0"
        var idLong = 0L
        try {
          idLong = id.toLong
        }
        val news = Post.find(id) match {
          case Full(n) => n
          case _ => S.redirectTo("/index")
        }
        var n: NodeSeq = <div class="shortnews">
                           <h2><span>{ news.title }</span></h2>
                           <p class="info">
                             <img src="style/images/author_mini.png"/>
                             { news.author.obj.get.getFullName }
                             <img src="style/images/data_mini.png"/>{ Formater.formatTime(news.createdAt.is) }
                           </p>
                           <br class="spacer"/>
                           { Unparsed(news.body.is) }
                           { if (isLoged && isAdmin && isOwner(news.author.obj.get.id.is)) <p id="editlink"><br/><a href={ "editpost?id=" + news.id.toString }>.::Edytuj stronę::.</a></p> }
                         </div>
        n
      }

      /**
       * wycinanie początku z artykułu aby umieścić do skrótu wiadomości  - do zrobienia!?
       * na razie wycina pierwszy akapit
       */
      def createShortNews(str: String): String = {
        (PCDataXmlParser("<div>" + str + "</div>").openOr(Text(str)) \ "p") match {
          case n: NodeSeq => if (n.isEmpty) "Brak treści!" else n.head.text
          case _ => "Niemożliwe?"
        }

      }

      /*wcześniej trzeba sprawdzić czy isLoged
  @param:id - id usera autora postu! */
      def isOwner(id: Long): Boolean = if (id == User.currentUser.get.id.is) true else false

    }

  }
} //end packages
