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

    import net.liftweb.mapper._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.lib.Formater

    class Pupil extends LongKeyedMapper[Pupil] with IdPK {
      def getSingleton = Pupil

      //object schoolId extends MappedString(this, 12)
      object user extends MappedLongForeignKey(this, User)
      object secondName extends MappedString(this, 30)
      object classIn extends MappedLongForeignKey(this, ClassModel)
      object birthDate extends MappedDate(this)
      object birthPlace extends MappedString(this, 30)
      object birthDistrict extends MappedString(this, 20)
      object pesel extends MappedString(this, 11)
      object address extends MappedString(this, 80)
      object nr extends MappedInt(this)
      object father extends MappedLongForeignKey(this, User)
      object mather extends MappedLongForeignKey(this, User)
      def fullInfoString() = {
        val theClass = this.user.obj.open_!
        this.id.is.toString + " " + theClass.getFullName + " [" +  Formater.formatDate(this.birthDate.is) + "] "}                       
    }

    object Pupil extends Pupil with LongKeyedMetaMapper[Pupil] {
    }

  }
}