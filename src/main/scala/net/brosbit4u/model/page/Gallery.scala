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

    import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._

    class Gallery extends LongKeyedMapper[Gallery] with IdPK {
      def getSingleton = Gallery

      //object nrPhoto extends MappedString(this,3) //nr własny galerii
      //object id_gallery extends MappedString(this,120) //id galerii
      object description extends MappedText(this) //opis
      object nrOfPhotos extends MappedString(this, 3) //ilość zdjęć
      object thumb extends MappedText(this)
      object title extends MappedString(this, 150)
      object urls extends MappedText(this)
    }

    object Gallery extends Gallery with LongKeyedMetaMapper[Gallery] {

    }

  }
}
