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

package net.brosbit4u.snippet

import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
import _root_.scala.xml.{ NodeSeq, Text, XML }
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import net.brosbit4u.model._

class ParentDataSn extends BaseTeacher {
  /*
	def dataTable() = {
	val idClass = ClassChoose.is
	if (idClass == 0) S.redirectTo("teacher/index")
    val classT = ClassModel.find(idClass)
    val pupils = Pupil.findAll(By(Pupil.classIn, classT))
   
    "tbody" #> pupils.map(pupil => {
		val user = pupil.user.obj.open_!
        val userMather = pupil.mather.obj.openOr(User.create)
		val userFather = pupil.father.obj.openOr(User.create)
        "tr" #> <tr id={"0" + pupil.id.is.toString}><td rowspan="2">{pupil.nr.is.toString}</td><td rowspan="2">{user.getFullNameReverse}</td>
				<td>O</td><td>{userFather.lastName.is}</td><td>{userFather.email.is}</td><td>{userFather.passStr.is}</td>
         <td>{userFather.phone.is}</td></tr>
		<tr id={"M"+userMather.id.is.toString}><td>M</td><td>{userMather.lastName.is}</td><td>{userMather.email.is}</td><td>{userMather.passStr.is}</td>
         <td>{userMather.phone.is}</td>
		 </tr>
    })
  }
  
  //poprawić
  def formItem() = {
        var dataStr = ""
        def processEntry(){
          val idClassStr = S.param("class").openOr("0")
          val user = User.currentUser.open_! 
          val classT = ClassModel.findAll(By(ClassModel.teacher, user))
          
          ///sprawdź czy to wychowawca danego ucznia
      
      
          val xml = XML.loadString(dataStr)
          (xml \ "user").map(userXml => {
			//pupilId
            val idPupil = (userXml \ "@id").toString
            val fistName =(userXml \ "firstName").text
			val lastName = (userXml \ "lastName").text
            val password = (userXml \ "password").text
            val phone = (userXml \ "phone").text
            val email = (userXml \ "email").text
            val address = (userXml \ "address").text
            val pupil = Pupil.find(idPupil).open_!
         /////dodawanie rodziców -------------- zaimplementować
            
          })
        }
        
         "#dataEdit" #> SHtml.text(dataStr, (x) => dataStr = x, "id" -> "dataEdit", "type" -> "hidden") &
         "#submit" #> SHtml.submit("", processEntry, "style" -> "display:none;")
      }
      */
}
