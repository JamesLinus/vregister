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
  import net.brosbit4u.model._
  import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
  import Helpers._
  import  _root_.net.liftweb.http.js.JsCmds._
  import  _root_.net.liftweb.http.js.JsCmd
  import  _root_.net.liftweb.http.js.JE._

    class ClassSn extends {


      def teachers()  = User.findAll(By(User.role, "n")).filter(teacher => !teacher.scratched).
    		  map(teacher => {
    		    val teacherString = teacher.shortInfo
    		    (teacherString,teacherString)
    		  })

      def classList() = {

        //var out:NodeSeq = NodeSeq.Empty
        val classList: List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level, Ascending))
        //val user:List[User] = User.find(class.id.is).get
        "tr" #> classList.map(theClass => {
          "tr [class]" #> { if (theClass.scratched.is) "scratched" else "" } &
            ".id" #> <td>{ theClass.id.is.toString }</td> &
            ".level" #> <td>{ theClass.level.is.toString }</td> &
            ".division" #> <td>{ theClass.division.is }</td> &
            ".teacher" #> <td>{theClass.teacher.obj match {
              case Full(teacher) => teacher.shortInfo
              case _ => "Brak"
            }}</td> &
            ".description" #> <td>{ theClass.descript.is }</td>
        })
      }

      def editAjax() = {
    var id = ""
    var level = ""
    var division = ""
    var teacher = ""
    var description = ""
      
    def save() =  {
      val theClass = ClassModel.find(id).openOr(ClassModel.create)
     ///insert
      JsFunc("insertRow", id).cmd
    }
    
    def delete() = {
      ClassModel.find(id) match {
        case Full(theClass) => {
          theClass.scratched(true).save
          JsFunc("deleteRow", id).cmd
        }
        case _ => Alert("Nie ma takiej klasy")
      }
    }
    
   val teacherPairList = teachers()
   val levels = (0 to 6).toList.map(lev => (level,level))

    val form = "#id" #> SHtml.text(id, id = _, "readonly"-> "readonly") &
       "#level" #> SHtml.select(levels, Full("0"), level = _) &
        "#division" #> SHtml.text(division, division = _) &
       "#teacher" #> SHtml.select(teacherPairList,Full(""), teacher = _) &
       "#description" #> SHtml.textarea(description, description = _) &
       "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type"->"image", 
           "onclick" -> "return confirm('Na pewno usunąć klasę?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image",
          "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

      "form" #> (in => form(in))
       }
    }

  }
} //koniec packages
