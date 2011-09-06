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

import net.brosbit4u.model._
import net.liftweb.http._
import scala.xml.Text
import net.liftweb.common._
import net.liftweb.util.Helpers._

class RegisterSwitcherSn {
  def redir() = {
    User.currentUser match {
      case Full(user) => user.role.is match {
        case char: String if (char == "a" || char == "n") => S.redirectTo("/register/")
        case "s" => S.redirectTo("/secretariat/")
        case char: String if (char == "u" || char == "r") => S.redirectTo("/view/")
        case _ => S.redirectTo("/user_mgt/login")
      }
      case _ => S.redirectTo("/user_mgt/login")
    }
    "#info" #> Text("Błąd")
  }
}

