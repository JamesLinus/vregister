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

    import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
    import _root_.scala.xml.{ NodeSeq, Text, XML }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._
    import net.brosbit4u.model.{ User, SubjectName, SubjectChangeList }

    class SubjectSn {
      /*
      //tabela z przedmiotami
      def subjectList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val subjects: List[SubjectName] = SubjectName.findAll(OrderBy(SubjectName.nr, Ascending))
        "tr" #> subjects.map(subject => {
          "tr [class]" #> { if (subject.validated.is) "" else "scratched" } &
            ".iduser" #> <td>{ subject.id.is.toString }</td> &
            ".name" #> <td>{ subject.name.is }</td> &
            ".short" #> <td>{ subject.short.is }</td> &
            ".nr" #> <td>{ subject.nr.is.toString }</td>
        })
      }
      
      def logedInUser() = "#username" #> Text(User.currentUser.open_!.getFullName)

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
            val nr = (userXml \ "@nr").text.toInt
            val name = (userXml \ "name").text
            val short = (userXml \ "shortName").text
            val idInt = try { id.toInt } catch { case _ => 0 }
            val subjectName = SubjectName.find(id).openOr(SubjectName.create)
            if (validated) {
              if (idInt > 0 && (name != subjectName.name.is || short != subjectName.short.is)) {
                val scl = SubjectChangeList.create
                scl.name(subjectName.name.is).short(subjectName.short.is).nr(subjectName.nr.is).
                  date(date).save
              }
              subjectName.name(name).short(short).nr(nr).
                validated(true).save
            } else {
              if (idInt > 0) subjectName.validated(false).save
            }
          })
        }

        "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
          "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")

      } */
    }

  }
} // koniec packages

