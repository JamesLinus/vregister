/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u { 
  package snippet {

import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
import _root_.scala.xml.{ NodeSeq, Text, XML }
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S, SessionVar }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import net.brosbit4u.model._

object ClassChoose extends SessionVar[Int](0)

class MainTeacher  {
  
		def logedInUser() = {
		  "#username" #> Text(User.currentUser.open_!.getFullName) &
		  "#choosenclass" #> { 
				if(ClassChoose.is == 0) Text("Wybierz klasę!")
				else {
						val cl = ClassModel.find(ClassChoose.is)
						if (cl.isEmpty) Text("Wybierz klasę!!")
						else Text(cl.open_!.classString)
				}
		  }
		}
	  
  
	def classList() = {
		val classParamStr = S.param("class").openOr("0")
		val paramClass = try{ classParamStr.toInt } catch {case e => 0}
		if(paramClass != 0) {
			ClassChoose.set(paramClass)
		}
		val classes = ClassModel.findAll(OrderBy(ClassModel.level, Ascending)).filter(_.scratched.is)
		"a" #> classes.map(classItem => {
				"a" #> <a href={"/teacher/index/"+ classItem.id.toString}>{classItem.classString}</a>
			}) &
		"#username" #> Text(User.currentUser.open_!.getFullName) &
		"#choosenclass" #> { 
				if(ClassChoose.is == 0) Text("Wybierz klasę!")
				else {
						val cl = ClassModel.find(ClassChoose.is)
						if (cl.isEmpty) Text("Wybierz klasę!!")
						else Text(cl.open_!.classString)
				}
		} 
	} 


}

  }}

