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

    import _root_.scala.xml.{ NodeSeq, Text, XML }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.java.util.{ Date, Random, GregorianCalendar, TimeZone }
    //import net.brosbit4u.lib._
    import net.brosbit4u.model._
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._

    class ClassSn extends {

      /** zwraca zawrtość seclect z nauczycielami*/ //do przydzielania wychowawców 
      def teachers(in: NodeSeq): NodeSeq = {
        val teacherList: List[User] = User.findAll(By(User.role, "n"))
        val option = <select id="teachersAdd">{
          for (teacher <- teacherList) yield {
            <option>{ teacher.firstName.is(0) + "." + teacher.lastName.is + " (" + teacher.id.is.toString() + ")" }</option>
          }
        }</select>

        option
      }

      def logedInUser() = "#username" #> Text(User.currentUser.open_!.getFullName)

      /** zwraca listę klas w tabeli*/
      def classList() = {

        //var out:NodeSeq = NodeSeq.Empty
        val classList: List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level, Ascending))
        //val user:List[User] = User.find(class.id.is).get
        "tr" #> classList.map(theClass => {
          "tr [class]" #> { if (theClass.validated.is) "" else "scratched" } &
            ".iduser" #> <td>{ theClass.id.is.toString }</td> &
            ".level" #> <td>{ theClass.level.is.toString }</td> &
            ".division" #> <td>{ theClass.division.is }</td> &
            ".descript" #> <td>{ theClass.descript.is }</td>
        })
      }

      /** dodanie formatki i obsługa */
      def formItem() = {
        var dataStr = ""

        def processEntry() {
          //println(dataStr);
          //zakłądam że  przyjdzie prawidłowy string lub go nie ma
          //S.notice(dataStrEA)
          //val date = new Date()
          val date = new GregorianCalendar(TimeZone.getTimeZone("Europe/Warsaw")).getTime
          val xml = XML.loadString(dataStr)
          (xml \ "user").map(userXml => {
            val id = (userXml \ "@id").toString
            //println("Found id ::::::::::: " + id)
            val validated = if ((userXml \ "@scratch").toString == "t") false else true;
            val level = (userXml \ "level").text.toInt
            val division = (userXml \ "division").text
            val descript = (userXml \ "descript").text
            val idInt = try { id.toInt } catch { case _ => 0 }
            val theClass = ClassModel.find(id).openOr(ClassModel.create)
            if (validated) {
              if (idInt > 0 && (level != theClass.level.is || division != theClass.division.is)) {
                val ccl = ClassChangeList.create
                ccl.level(theClass.level).descript(theClass.descript).division(theClass.division).
                  date(date).save
              }
              theClass.level(level).descript(descript).division(division).
                validated(true).save
            } else {
              if (idInt > 0) theClass.validated(false).save
            }
          })
        }

        "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
          "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")

      }
    }

  }
} //koniec packages
