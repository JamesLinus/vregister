/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
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
  package lib {

    import java.util.{ Date, Locale, GregorianCalendar }
    import java.text.{ SimpleDateFormat }

    /** Formatowanie daty */

    object Formater {
      def formatTime(t: Date): String = {
        val l = new Locale("pl")
        val sfd = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", l)
        sfd.format(t)
      }
      
      def pretyDate(t:Date): String = {
        val l = new Locale("pl")
        val sfd = new SimpleDateFormat("EEE, dd MMM yyyy", l)
        sfd.format(t)
      }
      
      def formatDate(t:Date):String = {
        val l = new Locale("pl")
        val sfd = new SimpleDateFormat("yyyy-MM-dd",l)
        sfd.format(t)
      }
      //nie napisana!!!
      def fromStringToDate(strDate:String):Date = {
        val listDate = strDate.split("-")
        if (listDate.length == 3){
          val year::month::day::rest = listDate.map(x => x.toInt).toList
          val gregorianCal = new GregorianCalendar(year, month, day)
          gregorianCal.getTime
        }
        else
          new Date()
        
      }
    }

  }
} //end packages
