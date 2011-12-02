
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
 *   
 */

package net.brosbit4u.snippet

import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
import _root_.scala.xml.{ NodeSeq, Text, XML }
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S, SessionVar }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import net.brosbit4u.model._

object ClassChoose extends SessionVar[Int](0)

class MainTeacher extends BaseTeacher {
  
	def classList() = {
		val paramClass = S.param("class").openOr("0").toInt
		if(paramClass != 0) {
			ClassChoose.set(paramClass)
		}
		val classes = ClassModel.findAll(OrderBy(ClassModel.level, Ascending)).filter(_.validated.is)
		"a" #> classes.map(classItem => {
				"a" #> <a href={"/teacher/index/"+ classItem.id.toString}>{classItem.classString}</a>
			})
	}


}
