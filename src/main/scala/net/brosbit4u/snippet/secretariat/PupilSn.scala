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

    import _root_.scala.xml.{ NodeSeq, Text }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._
    import net.brosbit4u.model.{ Pupil, User, ClassModel }

    class PupilSn {
      def pupilList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val pupils: List[Pupil] = Pupil.findAll() //jak ustawić kolejność od nazwiska???
        "tr" #> pupils.map(pupil => {
          val user = pupil.user.obj.openOr(User.create)
          "tr [class]" #> { if (pupil.user.obj.open_!.validated.is) "" else "scratched" } &
            ".iduser" #> <td>{ user.id.is.toString }</td> &
            ".reg" #> <td>{ pupil.id.is.toString }</td> &
            ".firstname" #> <td>{ user.firstName.is }</td> &
            ".lastname" #> <td>{ user.lastName.is }</td> &
            ".bithday" #> <td>{ pupil.birthDay.is }</td> &
            ".pesel" #> <td>{ pupil.pesel.is }</td>
            
        })
      }

      /** dodanie formatyki i obsługa */
      def formItem() = {
        

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

