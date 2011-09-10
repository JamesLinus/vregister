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

    class AssignationSn {
      //tabela z przedmiotami
      def bringupList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val classes = ClassModel.findAll(OrderBy(ClassModel.level, Ascending)).filter(_.validated.is)
        
        "tr" #> classes.map(classItem => {
            val teacher = classItem.teacher.obj match {
              case Full(t) => t.shortInfo
              case _ => "brak"
            }           
            ".classes" #> <td>{ classItem.shortInfo }</td> &
            ".teacher" #> <td>{teacher}</td> 
        })
      }
      
      def logedInUser() = "#username" #> Text(User.currentUser.open_!.getFullName)
      
      def teacherSelect() = {
        val infoTeacher:List[String] = User.findAll(By(User.role, "n")).map(_.shortInfo)
        <select style="display: none;" id="hiddenSelect">
          <option>brak</option>
          {infoTeacher.map(info => {
                <option>{info}</option>
              })}
        </select>
      }

      /** dodanie formatki i obsługa */
      def formItemBringup() = {
        var dataStr = ""

        def processEntry() {
          //println("dataStr: " + dataStr);
          //zakłądam że  przyjdzie prawidłowy string lub go nie ma
          //S.notice(dataStrEA)
          //val date = new Date()
          //val date = new GregorianCalendar(TimeZone.getTimeZone("Europe/Warsaw")).getTime
          val xml = XML.loadString(dataStr)
          (xml \ "theclass").map(userXml => {
            val idClass = (userXml \ "@idClass").toString
            val idTeacher = (userXml \ "@idTeacher").toString
            val classModel = ClassModel.find(idClass).open_!
            val teacher = User.find(idTeacher).open_!
            if (teacher.role.is == "n"){
              classModel.teacher(teacher).save
            }
            else println("BŁĄD!!! Przydzielono użytkownika - nienauczyciela na wychowawcę.")
             
          })
        }

        "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
          "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")

      }
      
      def pupilJSONdata(in:NodeSeq):NodeSeq = {
        
        <script></script>
      }
      
      def formItemPupil() = {
        var dataStr = ""
        def processEntry(){
          
        }
        
         "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
         "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")
      }
      
      def classSelect() = {
        val classes = ClassModel.findAll().map(_.classString)
        "#selectChoise" #> <select id="selectChoise" onchange="changeClass()">
          <option>wybierz</option>
          {classes.map(clas => <option>{clas}</option>)}
                           </select>
      }
      
      
    
     }
  
  }} // koniec packages

