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

import _root_.scala.xml.{NodeSeq}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.brosbit4u.model._
//import _root_.net.liftweb.mapper.{Descending ,OrderBy,By}
//import _root_.net.liftweb.http.{S}
//import Helpers._

    trait UsersOperations {

     var isLoged = false
      var isTeacher = false
      var isAdmin = false
      val currentUserName = User.currentUser match {
        case Full(user) => {
            isLoged = true
            isTeacher = if (user.role.is == "n" || user.role.is == "a") true else false
            isAdmin = if(user.role.is == "a") true else false
            user.firstName + " " + user.lastName
          }
        case _ => "Niezalogowany"
      }
      

      def logInfo(node:NodeSeq):NodeSeq = {
        if (isLoged) <a href="/user_mgt/logout"><table border="0"><tr><td><img src="style/images/loged.png" /> </td><td>{currentUserName}</td></tr></table> </a>
        else <a href="/user_mgt/login"><table border="0"><tr><td><img src="style/images/nologed.png" /> </td><td>Niezalogowany</td></tr></table></a>
      }



    }

  }} //ends packages