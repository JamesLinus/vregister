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

    import _root_.scala.xml.{NodeSeq, Text, XML}
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{SHtml,S}
    import _root_.net.liftweb.common._
    import _root_.java.util.{Date,Random}
    import _root_.net.liftweb.mapper.{By,OrderBy,Ascending}
    import Helpers._
    import net.brosbit4u.model._

    class AddTeacherSn {
      
      def teacherList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val teachers:List[User] = User.findAll(By(User.role,"n"),OrderBy(User.lastName, Ascending))

        "tr" #> teachers.map(teacher => {
            "#firstname" #> <td>{teacher.firstName}</td> &
            "#lastname" #> <td>{teacher.lastName}</td> &
            "#email" #> <td>{teacher.email.is}</td> &
            "#password" #> <td>{teacher.passStr}</td> &
            "#phone" #> <td>{teacher.phone}</td> 
          })
        
      }

      /** dodanie formatki i obsługa */
      def formItem() = {
        var dataStrE = ""
        var dataStrN = ""
        var dataStrD = ""

        def processEntry() {
          //zakłądam że  przyjdzie prawidłowy string lub go nie ma
         //S.notice(dataStrEA)


          val lines = dataStrE.split(";")
          val timeNowL = new Date()
          //val timeNow = new GregorianCalendar(TimeZone.getTimeZone("Europe/Warsaw")).getDate
          for (line <- lines) {
            //S.notice(line)
            if (line.size > 5) {
              val data = line.split(",")
              val id = data(0)
              
             
              //edycja nauczyciela
             
                val tFull = User.find(id)
                if (!tFull.isEmpty) {
                  val t = tFull.get
                  S.notice(t.lastName.is)
                  t.lastName(data(1))
                  t.firstName(data(2))
                  if (data(3) != "--------") {
                    t.passStr(data(3))
                    t.password(data(3))
                    t.timePass(timeNowL)
                  }
                  t.email(data(4))
                  t.phone(data(5))
                  t.save
                }
              
            }
          }
          var lines2 = dataStrN.split(";")
          for (line <- lines2) {
            if (line.size > 5) {
              val data = line.split(",")
              val id = data(0).toInt
              if (id < 0 ) {
                val t = User.create
                t.lastName(data(1))
                t.firstName(data(2))
                t.passStr(data(3))
                t.password(data(3))
                t.timePass(timeNowL)
                t.email(data(4))
                t.phone(data(5))
                t.role("n")
                t.validated(true).save
              }
            }
          }

          
          val idToDel = dataStrD.split(';')

          for(id <- idToDel) {
            if (id.size > 0) {
              val t = User.find(id.toInt).get
              t.delete_!
            }
          }
          

        }
                     "#dataEdit" #> SHtml.text(dataStrE,(x) => dataStrE = x, "id" -> "dataEdit", "type" -> "hidden") &
                     "#dataNew" #> SHtml.text(dataStrN,(x) => dataStrN = x, "id" -> "dataNew", "type" -> "hidden") &
                     "#dataDelete" #> SHtml.text(dataStrD, (x) => dataStrD = x, "id"-> "dataDelete","type"-> "hidden") &
                     "#submit" #> SHtml.submit("Zapisz zmiany!", processEntry, "onclick" -> "createData()")

      }


      def sketchTeacher(xmlStr:String) {
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

  }} //end packages
