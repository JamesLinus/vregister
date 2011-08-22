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

 import _root_.scala.xml.{NodeSeq, Text}
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{SHtml,S}
    import _root_.net.liftweb.common._
    import _root_.java.util.{Date,Random}
    //import net.brosbit4u.lib._
    import net.brosbit4u.model.{User,ClassModel}
    import _root_.net.liftweb.mapper.{By,OrderBy,Ascending}
    import Helpers._

class ClassSn {
  
  /** zwraca zawrtość seclect z nauczycielami*/
  def teachers(in:NodeSeq):NodeSeq = {
    val teacherList:List[User] = User.findAll(By(User.role, "n"))
    val option = <select id="teachersAdd" >{for (teacher <- teacherList) yield {
        <option>{teacher.firstName.is(0) + "." + teacher.lastName.is + " (" + teacher.id.is.toString() + ")"}</option>
        }
                }  </select>
   
    option
  }
  
   /** zwraca listę klas w tabeli*/   
  def classList(in:NodeSeq):NodeSeq = {
    
        //var out:NodeSeq = NodeSeq.Empty
        val classList:List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level,Ascending))
        //val user:List[User] = User.find(class.id.is).get
        val cont = <tbody id="list">{for (classObj <- classList) yield {
              <tr id={classObj.id.is.toString} >
                <td >{classObj.level.is.toString}</td>
                <td >{classObj.letters.is}</td>
                <td>{classObj.description.is}</td>
                <td>{classObj.teacher.obj.get.getFullName}</td>
                <td><button  name="edit" onclick="addEditInputs(this)">Edytuj!</button></td>
              </tr> //nieprawidłowe id w cudzysłowiu
            }
          } </tbody>
        cont
  }
     

 /** dodanie formatki i obsługa */
      def formItem(in:NodeSeq):NodeSeq = {
        var dataStrE = ""
        var dataStrD = ""
        var dataStrN = ""

        def processEntry() {
          //zakłądam że  przyjdzie prawidłowy string lub go nie ma
         //S.notice(dataStrEA)

          val lines1 = dataStrN.split(";")
          for (line <- lines1) {
            if (line.size > 5) {
              val data = line.split(",")
              val id = data(0).toInt
              if (id < 0) {
                val c = ClassModel.create
                c.level(data(1).toInt)
                c.letters(data(2))
                c.description(data(3))
                c.teacher(data(4).split("\\(")(1).split("\\)")(0).toLong) //t??????
                c.save
              }
            }
          }


          val lines2 = dataStrE.split(";")
          for (line <- lines2) {
            if (line.size > 5) {
              val data = line.split(",")
              val id = data(0).toInt
              if (id > 0) {
                val c = ClassModel.find(id).get
                //S.notice(c.sureName.is)
                c.level(data(1).toInt)
                c.letters(data(2))
                c.description(data(4))        
                c.teacher(data(4).split("\\(")(1).split("\\)")(0).toLong) //t??????
                c.save
              }
            }
          }

          val idToDel = dataStrD.split(';')

          for(id <- idToDel) {
            if (id.size > 0) {
              val c = ClassModel.find(id.toInt).get
              c.delete_!
            }
          }


        }
        Helpers.bind("entry",in,
                     "dataE" -> SHtml.text(dataStrE,(x) => dataStrE = x, "id" -> "dataE", "type" -> "hidden" ),
                     "dataN" -> SHtml.text(dataStrN,(x) => dataStrN = x, "id" -> "dataN", "type" -> "hidden"),
                     "dataD" -> SHtml.text(dataStrD, (x) => dataStrD = x, "id"-> "dataD","type"-> "hidden"),
                     "submit" -> SHtml.submit("Zapisz zmiany!", processEntry, "onclick" -> "createData()"))

      }
}

    
  }} //koniec packages
