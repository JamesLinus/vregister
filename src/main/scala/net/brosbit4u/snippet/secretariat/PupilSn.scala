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
    import net.brosbit4u.model._
    import net.brosbit4u.lib.Formater

    class PupilSn {
      def pupilList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val pupils: List[Pupil] = Pupil.findAll() //jak ustawić kolejność od nazwiska???
        "tr" #> pupils.map(pupil => {
          val user = pupil.user.obj.openOr(User.create)
          "tr [class]" #> { if (pupil.user.obj.open_!.validated.is) "" else "scratched" } &
            ".iduser" #> <td>{ pupil.id.is.toString }</td> &
            ".firstname" #> <td>{ user.firstName.is }</td> &
            ".lastname" #> <td>{ user.lastName.is }</td> &
            ".birthdate" #> <td>{ Formater.formatDate(pupil.birthDate.is) }</td> &
            ".pesel" #> <td>{ pupil.pesel.is }</td>
            
        })
      }
      
      def logedInUser() = "#username" #> Text(User.currentUser.open_!.getFullName)

      /** dodanie formatyki i obsługa */
      def formItem() = {
        var dataStr = ""
        
        def processEntry(){
          val date = new GregorianCalendar(TimeZone.getTimeZone("Europe/Warsaw")).getTime
          val xml = XML.loadString(dataStr)
          (xml \ "user").map(userXml => {
            val id = (userXml \ "@id").toString
            //println("Found id ::::::::::: " + id)
            val validated = if ((userXml \ "@scratch").toString == "t") false else true;
            val firstName = (userXml \ "firstName").text
            val lastName = (userXml \ "lastName").text
            val birthDateStr = (userXml \ "birthDate").text
            val pesel = (userXml \ "pesel").text
            val birthDate = Formater.fromStringToDate(birthDateStr)
            val idInt = try { id.toInt } catch { case _ => 0 }
            val pupil = Pupil.find(id).openOr(Pupil.create)
            val user = pupil.user.obj.openOr(User.create)
            if (validated) {
              if (idInt > 0 ) {
                val pcl = PupilChangeList.create
                val ucl = UserChangeList.create
                if (user.firstName != firstName || user.lastName != lastName){
                  ucl.firstName(user.firstName.is).lastName(user.lastName.is).email(user.email.is).
                    passStr(user.passStr.is).phone(user.phone.is).user(user).date(date).save
                    user.firstName(firstName).lastName(lastName).validated(true).save
                }
                else {
                  if (user.validated.is == false) user.validated(true).save
                }
                if (pupil.birthDate.is.getTime != birthDate.getTime || pupil.pesel.is != pesel){
                  pcl.birthDate(pupil.birthDate.is).pesel(pupil.pesel.is).nr(pupil.nr.is).
                  date(date).save
                  pupil.birthDate(birthDate).pesel(pesel).save
                }
                
              }
              else {
                user.firstName(firstName).lastName(lastName).role("u").validated(true).save
                pupil.birthDate(birthDate).pesel(pesel).user(user).save
              }
              
            } else {
              if (idInt > 0) user.validated(false).save
            }
          })
          
        }
        
        "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
        "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")

      }

//      def orderForm() = {
//        var classStr = ""
//        def makeOrder() {
//          val clId = classStr.toLong
//          val pL = Pupil.findAll(By(Pupil.classIn, clId)).map(p => p.user.obj.get).sortWith((a, b) => if (a.lastName.is == b.lastName.is) a.firstName.is < b.firstName.is else a.lastName.is < b.lastName.is)
//          var numb = 0;
//          pL.foreach(u => {
//            val p = Pupil.findAll(By(Pupil.user, u))
//            if (!p.isEmpty) {
//              numb += 1
//              p.head.nr(numb).save
//            }
//          })
//        }
//        val classList: List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level, Ascending), OrderBy(ClassModel.division, Ascending))
//        val classesL: List[(String, String)] = ClassModel.findAll(OrderBy(ClassModel.level, Ascending), OrderBy(ClassModel.division, Ascending)).map(c => (c.id.is.toString, c.level.is + c.division.is + "(" + c.id.is.toString + ")"))
//        bind("F", in, "classmenu" -> SHtml.select(classesL, Full(classesL.head._2), c => classStr = c),
//          "submit" -> SHtml.submit("Ułóż!", makeOrder, "onclick" -> "return areyousure()"))
//      }
    }

  }
} // koniec packages

