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

    import _root_.scala.xml.{ NodeSeq }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import _root_.net.liftweb.mapper.{ By }
    import _root_.net.liftweb.http.{ SHtml }
    import Helpers._
    import net.brosbit4u.lib.{ PicasaIndex }

    class PicasaSn {
      def make(): Boolean = {
        val picasa = new PicasaIndex()
        if (picasa.make) true
        else false
      }

      def forms() = {
        val edAddress = ExtraData.findAll(By(ExtraData.keyStr, "picasamail")) match {
          case Nil => ExtraData.create
          case list => list.head
        }
        val edPass = ExtraData.findAll(By(ExtraData.keyStr, "picasapass")) match {
          case Nil => ExtraData.create
          case list => list.head
        }
        var pass: String = edPass.dataStr.is
        var address = edAddress.dataStr.is
        def save() {
          if (pass.trim.length > 0 && address.trim.length > 0) {
            edAddress.keyStr("picasamail").dataStr(address).save
            edPass.keyStr("picasapass").dataStr(pass).save
          }
        }

        "#adres" #> SHtml.text(address, x => address = x) &
          "#pass" #> SHtml.text(pass, x => pass = x, "type" -> "password") &
          "#submit" #> SHtml.submit("Zapisz!", save) &
          "#index" #> SHtml.submit("Indeksuj!", make)
      }
    }

  }
} //end pakages
