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

    class SubjectName extends LongKeyedMapper[SubjectName] with IdPK {
      def getSingleton = SubjectName

      object name extends MappedString(this, 40)
      object short extends MappedString(this, 5)
      object nr extends MappedInt(this)
      object scratched extends MappedBoolean(this)
    }

    object SubjectName extends SubjectName with LongKeyedMetaMapper[SubjectName] {

    }

  }
}