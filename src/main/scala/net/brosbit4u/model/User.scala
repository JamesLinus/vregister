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
  package model {

    import _root_.net.liftweb.http.S
    import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import Mailer._

    /**
     * The singleton that has methods for accessing the database
     */
    object User extends User with MetaMegaProtoUser[User] {
      override def dbTableName = "users" // define the DB table name

      // define the order fields will appear in forms and output
      override def fieldOrder = List(id, firstName, lastName, email,
        locale, timezone, password, role)
      override def screenWrap = Full(<lift:surround with="default" at="content">
                                       <div id="main" class="main-section">
    		  							<div class="container_12"><lift:bind/>
    		  							</div></div>
                                     </lift:surround>)
      // comment this line out to require email validations
      override def skipEmailValidation = false
      
       override def createUserMenuLoc = Empty
       override def editUserMenuLoc = Empty
       
       override def afterCreate = List(user => Mailer.sendMail(From("automat"), Subject("konto szkolne"), 
           To(user.email.is),XHTMLMailBodyType(<div>
        		   <h2>{"Witaj " + user.getFullName}</h2>
        		   <p>Założono nowe konto w XX LO, aby dokończyć rejestrację wejdź na poniższy link</p>
        		   <a href={"http//xxlo.pl/user_mgt/validate_user/" + user.userIdAsString }></a>
        		   </div>)))
      
      override def afterValidation  = List( (user) => { S.redirectTo("/user_mgt/change_password")})
     
    }

    /**
     * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
     */
    class User extends MegaProtoUser[User] {
      def getSingleton = User // what's the "meta" server

      object role extends MappedString(this, 1)
      object scratched extends MappedBoolean(this){
         override def defaultValue = false
      }
      object phone extends MappedString(this, 12)
      //object passStr extends MappedString(this, 12)
      //object timePass extends MappedDateTime(this)

      def getFullName = firstName.is + " " + lastName.is
      def getFullNameReverse = lastName.is + " " + firstName.is
      def shortInfo = getFullName + " [" + id.is.toString + "]"

    }

  }
}
