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

  class ForumThread extends LongKeyedMapper[ForumThread] with IdPK with OneToMany[Long, ForumThread]  {
      def getSingleton = ForumThread

      object title extends MappedString(this,120)
      object department extends MappedLongForeignKey(this, ForumDep)
      object author extends MappedLongForeignKey(this, User)
      object lastPoster extends MappedLongForeignKey(this, User)
      object lastTime extends MappedDateTime(this)
      object comments extends MappedOneToMany(ForumCom,ForumCom.thread, OrderBy(ForumCom.createdAt,Ascending) )
  }

  object ForumThread extends ForumThread with LongKeyedMetaMapper[ForumThread] {

  }

  }} //ends packages


