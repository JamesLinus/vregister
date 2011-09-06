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
    import net.brosbit4u.lib._
    //import _root_.net.liftweb.mapper.{Descending ,OrderBy,By}
    import _root_.net.liftweb.http.{ SHtml }
    import Helpers._

    class ContactSn {

      val cmList = ContactMail.findAll
      def cmListDB = cmList.map(x => (x.mail.is -> x.descript.is))
      val cmListPair = ("-----" -> "-----") :: cmListDB

      def getData() = {
        var theme = ""
        var content = ""
        var sign = ""
        var mail = ""
        var selectedMail = ""

        def sendMail() {
          val emailer = new Emailer()
          val body = content + "\n" + "----------\n" + sign
          emailer.sendEmail(selectedMail, theme, content)

        }

        "#theme" #> SHtml.text(theme, x => theme = x) &
          "#content" #> SHtml.textarea(content, x => content = x, "id" -> "") &
          "#sign" #> SHtml.text(sign, x => sign = x) &
          "#mail" #> SHtml.text(mail, x => mail = x) &
          "#select" #> SHtml.select(cmListPair, Empty, x => selectedMail = x) &
          "#submit" #> SHtml.submit("Wyślij!", sendMail)

      }
    }

  }
} //end packages
