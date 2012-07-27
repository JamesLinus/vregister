/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   Licence AGPL v3 see http://www.gnu.org/licenses/
 */

package net.brosbit4u.snippet.page

import _root_.scala.xml.{ NodeSeq }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import Helpers._

trait UsersOperations {

  var isLoged = false
  var isTeacher = false
  var isAdmin = false
  val currentUserName = User.currentUser match {
    case Full(user) => {
      isLoged = true
      isTeacher = if (user.role.is == "n" || user.role.is == "a") true else false
      isAdmin = if (user.role.is == "a") true else false
      user.firstName + " " + user.lastName
    }
    case _ => "Niezalogowany"
  }

  def loginInfo() = {
    val node = if (isLoged) <a href="/user_mgt/logout"> <span>{ currentUserName }</span> </a>
    else <a href="/user_mgt/login"><span>Niezalogowany</span></a>
    "a" #> node
  }

}
 