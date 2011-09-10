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
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._
    import net.brosbit4u.model._

    class AddTeacherSn {

      def teacherList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val teachers: List[User] = User.findAll(By(User.role, "n"))

        "tr" #> teachers.map(teacher => {
          "tr [class]" #> { if (teacher.validated.is) "" else "scratched" } &
            ".iduser" #> <td>{ teacher.id.is.toString }</td> &
            ".firstname" #> <td>{ teacher.firstName.is }</td> &
            ".lastname" #> <td>{ teacher.lastName.is }</td> &
            ".email" #> <td>{ teacher.email.is }</td> &
            ".password" #> <td>{ teacher.passStr.is }</td> &
            ".phone" #> <td>{ teacher.phone.is }</td>
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
            val firstName = (userXml \ "firstName").text
            val lastName = (userXml \ "lastName").text
            val email = (userXml \ "email").text
            val passStr = (userXml \ "password").text
            val phone = (userXml \ "phone").text
            val idInt = try { id.toInt } catch { case _ => 0 }
            val user = User.find(id).openOr(User.create)
            if (validated) {
              if (idInt > 0) {
                val ucl = UserChangeList.create
                ucl.firstName(user.firstName).lastName(user.lastName).email(user.email).
                  phone(user.phone).passStr(passStr).date(date).user(user).save
              }
              user.firstName(firstName).lastName(lastName).email(email).phone(phone).role("n").validated(true)
              if (passStr != "--------") user.passStr(passStr).password(passStr)
              user.save
            } else {
              if (idInt > 0) user.validated(false).save
            }
          })
        }

        "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
          "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")

      }

      def sketchTeacher(xmlStr: String) {
        val xml = XML.loadString(xmlStr)
        val allNodes = xml \ "sketch"
        allNodes.map(node => {
          val id = (node \ "@id").toString
          User.find(id) match {
            case Full(user) => user.validated(false)
            case _ =>
          }
        })

      }
    }

  }
} //end packages
