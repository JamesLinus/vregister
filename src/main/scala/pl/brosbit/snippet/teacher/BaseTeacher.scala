/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.snippet.teacher 

import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
import _root_.scala.xml.{ NodeSeq, Text, XML }
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S, SessionVar }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import pl.brosbit.model._

class BaseTeacher  {
   val user = User.currentUser.open_!
   
		def choosenClass() = {
		  if (ClassChoose.is == 0) S.redirectTo("/teacher/index") 
		  else "#choosenclass *" #> ClassString.is
		}
	  
	
	protected def isBriningUp = {	 
	  val theClass = ClassModel.find(ClassChoose.is.toLong).openOr(ClassModel.create)
	  if(user.id.is == theClass.teacher.obj.get.id.is || user.role == "a" ) true
	  else false
	}
	
	protected def isAdmin = user.role.is == "a"
	
}


