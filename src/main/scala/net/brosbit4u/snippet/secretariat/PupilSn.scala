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
package   snippet {


    import _root_.scala.xml.{NodeSeq, Text}
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{SHtml,S}
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper.{By,OrderBy,Ascending}
    import Helpers._
    import net.brosbit4u.model.{Pupil,User,ClassModel}


class PupilSn {
    def pupilList(in: NodeSeq):NodeSeq = {
        //var out:NodeSeq = NodeSeq.Empty
        val pupils:List[Pupil] = Pupil.findAll() //jak ustawić kolejność od nazwiska???
        val cont = <tbody id="list">{for (p <- pupils) yield {
              <tr id={p.user.obj.get.id.toString} >
                <td>{p.nr.is}</td>
                <td >{p.user.obj.get.lastName}</td>
                <td >{p.user.obj.get.firstName}</td>
                <td>{p.schoolId.is}</td>
                <td>{p.classIn.obj.get.classString}</td>
                <td><button onclick='addEditInputs(this)'>Edytuj!</button></td>
              </tr>
            }
          } </tbody>
        cont
      }

      /**  */
   def classes(in:NodeSeq):NodeSeq = {
     val classList:List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level,Ascending),OrderBy(ClassModel.letters,Ascending))
     val cont = <select id="classAdd" > {for (c <- classList)yield { <option>{c.level.is + c.letters.is + "("+ c.id.is.toString +")"}</option>} }
                </select>
       cont
   }
      /** dodanie formatyki i obsługa */
      def formItem(in:NodeSeq):NodeSeq = {
        var dataStrN = ""
        var dataStrE = ""
        var dataStrD = ""

        def processEntry() {
          //zakładam że  przyjdzie prawidłowy string lub go nie ma
         //S.notice(dataStrEA)

          val lines1 = dataStrN.split(";")
          val timeNowL = new java.util.Date() //aby dodać kiedy ostatnio zmieniano password
          for (line <- lines1) {
            //S.notice(line)
            if (line.size > 5) {
              val data = line.split(",")
              val id = data(0).toInt
              //jeżeli nowy nauczyciel          
                val u = User.create
                u.lastName(data(2))
                u.firstName(data(3))
                u.passStr("")
                u.timePass(timeNowL)
                u.role("u")
                
              //dodać pupil
              val p = Pupil.create
              val nr = try {
                  data(1).toInt
                }
                catch {
                  case _ => 0
                }
                p.nr(nr)
                p.schoolId(data(4))
              val idClass = data(5).split('(')(1).split(')')(0)
              val cFull = ClassModel.find(idClass)
              if (!cFull.isEmpty) {
                val c = cFull.get
                p.classIn(c)
                u.validated(true).save
                p.user(u)
                p.save
              }
              else {
                S.notice("Błędna klasa!")
              }
            }
          }
          //edycja ucznia
          val lines2 = dataStrE.split(",")
          for (line <- lines2) {
            if (line.size > 5) {
              val data = line.split(';')
              val id = data(0)
              val uFull = User.find(id)
              if (!uFull.isEmpty) {
                val u = uFull.get
                u.lastName(data(2))
                u.firstName(data(3))
                val p = Pupil.findAll(By(Pupil.user,u)).head //brak zabezpieczenia!
                 val nr = try {
                  data(1).toInt
                }
                catch {
                  case _ => 0
                }
                if (p.nr.is == 0) p.nr(nr)
                else if (nr > 0) p.nr(nr)
                p.schoolId(data(4))
                val idClass = data(5).split('(')(1).split(')')(0)
                val cFull = ClassModel.find(idClass)
                if (!cFull.isEmpty) {
                  val c = cFull.get
                  p.classIn(c)
                  u.save
                  p.save
                }
              }


            }
         }
          

          val idToDel = dataStrD.split(';')

          for(id <- idToDel) {
            if (id.size > 0) {
              // popracować
              val uFull = User.find(id)
              if (!uFull.isEmpty) {
                val u = uFull.get
                val p = Pupil.findAll(By(Pupil.user, u)).head //brak zabezpieczenia!
                  p.delete_!
                  u.delete_!
                }
                
              }

            
          }


        }
        Helpers.bind("entry",in,
                     "dataE" -> SHtml.text(dataStrE,(x) => dataStrE = x, "id" -> "dataE", "type" -> "hidden" ),
                     "dataN" -> SHtml.text(dataStrN,(x) => dataStrN = x, "id" -> "dataN", "type" -> "hidden" ),
                     "dataD" -> SHtml.text(dataStrD, (x) => dataStrD = x, "id"-> "dataD","type"-> "hidden"),
                     "submit" -> SHtml.submit("Zapisz zmiany!", processEntry, "onclick" -> "createData()"))

      }

      def orderForm(in:NodeSeq):NodeSeq = {
        var classStr = ""
        def makeOrder() {
          val clId = classStr.toLong
          val pL = Pupil.findAll(By(Pupil.classIn, clId)).map(p => p.user.obj.get).sortWith((a,b) => if (a.lastName.is == b.lastName.is) a.firstName.is < b.firstName.is else a.lastName.is < b.lastName.is )
          var numb = 0;
          pL.foreach( u => { val p = Pupil.findAll(By(Pupil.user, u))
                            if (!p.isEmpty) {
                            numb += 1
                            p.head.nr(numb).save
                            }
            }
          )
        }
        val classList:List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level,Ascending),OrderBy(ClassModel.letters,Ascending))
        val classesL:List[(String,String)] = ClassModel.findAll(OrderBy(ClassModel.level,Ascending),OrderBy(ClassModel.letters,Ascending)).map(c => (c.id.is.toString, c.level.is + c.letters.is + "("+c.id.is.toString + ")") )
        bind("F",in, "classmenu" -> SHtml.select(classesL, Full(classesL.head._2),c => classStr = c ),
             "submit" -> SHtml.submit("Ułóż!", makeOrder, "onclick" -> "return areyousure()"))
      }
}



}} // koniec packages




