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
    import net.brosbit4u.model.{User,SubjectName}


    class SubjectSn {
      //tabela z przedmiotami
      def subjectList(in: NodeSeq):NodeSeq = {
        //var out:NodeSeq = NodeSeq.Empty
        val subjects:List[SubjectName] = SubjectName.findAll(OrderBy(SubjectName.nr, Ascending))
        val cont = <tbody id="list">{for (s <- subjects) yield {
              <tr id={s.id.toString} >
                <td >{s.nr}</td>
                <td >{s.name}</td>
                <td>{s.short}</td>
                <td><button onclick="addEditInputs(this)">Edytuj!</button></td>
              </tr>
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

          val lines = dataStrN.split(";")
          for (line <- lines) {
            //S.notice(line)
            if (line.size > 4 ) { //jeżeli nie jest pusta linia
              val data = line.split(",")
              val id = data(0).toInt
              //jeżeli nowy przedmiot
              if (id < 1) {
                val s = SubjectName.create
                s.nr(data(1).toInt)
                s.name(data(2))
                s.short(data(3))
                s.save
              }
            }
            //edycja nauczyciela
            val lines2 = dataStrE.split(";")
            for (line <- lines2) {
              if (line.size > 4 ) {
                val data = line.split(",")
                val id = data(0).toInt
                val s = SubjectName.find(id).get
                //S.notice(s.name.is)
                s.nr(data(1).toInt)
                s.name(data(2))
                s.short(data(3))
                s.save
              }
            }
          }

          val idToDel = dataStrD.split(';')

          for(id <- idToDel) {
            if (id.size > 0) {
              val s = SubjectName.find(id.toInt).get
              s.delete_!
            }
          }


        }
        Helpers.bind("entry",in,
                     "dataE" -> SHtml.text(dataStrE,(x) => dataStrE = x, "id" -> "dataE", "type" -> "hidden" ),
                     "dataN" -> SHtml.text(dataStrN,(x) => dataStrN = x, "id" -> "dataN", "type" -> "hidden" ),
                     "dataD" -> SHtml.text(dataStrD, (x) => dataStrD = x, "id"-> "dataD","type"-> "hidden"),
                     "submit" -> SHtml.submit("Zapisz zmiany!", processEntry, "onclick" -> "createData()"))

      }
    }



  }} // koniec packages

