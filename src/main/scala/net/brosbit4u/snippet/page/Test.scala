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

    import _root_.scala.xml.{ NodeSeq, Text, Unparsed, XML }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
    import _root_.net.liftweb.http.{ S, SHtml }
    import Helpers._
    import java.io._

    class Test {

      def test(n: NodeSeq): NodeSeq = {

        <div>Sprawdzanie lightbox.</div>

      }
      def formTest(n: NodeSeq): NodeSeq = {
        var text = ""
        def nic() {

        }
        bind("f", n, "text" -> SHtml.text(text, x => text = x),
          "sub" -> SHtml.submit("OK", nic, "onclick" -> "actions();"))
      }

    }
  }
} //package end
