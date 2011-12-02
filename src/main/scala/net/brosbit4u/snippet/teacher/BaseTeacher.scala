/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.brosbit4u.snippet

import _root_.scala.xml.{ Text }
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S, SessionVar }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import net.brosbit4u.model._

class BaseTeacher {
		
		def logedInUser() = "#username" #> Text(User.currentUser.open_!.getFullName) &
		"#choosenclass" #> { 
				if(ClassChoose.is == 0) Text("Wybierz klasę!")
				else {
						val cl = ClassModel.find(ClassChoose.is)
						if (cl.isEmpty) Text("Wybierz klasę!!")
						else Text(cl.open_!.classString)
				}
	  
		}
}
