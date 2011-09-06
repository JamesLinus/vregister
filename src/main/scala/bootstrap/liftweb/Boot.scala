///*
// * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
// *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
// *
// *   VRegister is free software: you can redistribute it and/or modify
// *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
// *   as published by the Free Software Foundation
// *
// *   VRegister is distributed in the hope that it will be useful,
// *   but WITHOUT ANY WARRANTY; without even the implied warranty of
// *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *   GNU General Public License for more details.
// *
// *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
// *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
// */
//

package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.provider._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
//import net.brosbit4u.model.User
import Helpers._
import net.liftweb.mapper.{ DB, By, ConnectionManager, ConnectionIdentifier, Schemifier, DefaultConnectionIdentifier }
import java.sql.{ Connection, DriverManager }
import net.brosbit4u.model._

object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      /**
       * A class that's instantiated early and run.  It allows the application
       * to modify lift's environment
       */
      //Class.forName(classOf[org.postgresql.Driver].getName)
      Class.forName("org.postgresql.Driver")
      val dm = DriverManager.getConnection("jdbc:postgresql:vregister", "vregister", "123test456")
      Full(dm)
    } catch {
      case e: Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) { conn.close }
}

class Boot {
  def boot {

    //    if (!DB.jndiJdbcConnAvailable_?) {
    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    println("db added")

    // where to search snippet
    LiftRules.addToPackages("net.brosbit4u")

    Schemifier.schemify(true, Schemifier.infoF _, User, Post, Page,
      Department, ForumDep, FileStore, ContactMail, ForumCom,
      ForumThread, Gallery, ExtraData, LinkItem, LinkDepartment,
      ClassModel, UserChangeList, MarkMap, Pupil, SubjectName,
      ClassChangeList)

    //    val adminList = User.findAll(By(User.role, "a"))
    //    if (adminList.length == 0) {
    //      val u  = User.create
    //      u.lastName("Administrator").email("mail@mail.org").password("123qwerty").validated(true).save
    //    }

    if (DB.runQuery("select * from users where lastname = 'Administrator'")._2.isEmpty) {
      val u = User.create
      u.lastName("Administrator").role("a").password("123qwerty").email("mail@mail.org").validated(true).save
    }

    val loggedIn = If(() => User.loggedIn_? && User.currentUser.open_!.validated.is,
      () => RedirectResponse("/"))
    val isAdmin = If(() => User.loggedIn_? && (User.currentUser.open_!.role.is == "a"),
      () => RedirectResponse("/"))
    val isSecretariat = If(() => User.loggedIn_? && (User.currentUser.open_!.role.is == "s"),
      () => RedirectResponse("/"))

    // Build SiteMap::
    def sitemap() = SiteMap(
      List(
        Menu("Szkoła") / "index" >> LocGroup("public"), // Simple menu form
        Menu("Galeria") / "gallery" >> LocGroup("public"),
        Menu("Kontakt") / "contact" >> LocGroup("public"),
        Menu("Forum") / "forum" >> LocGroup("public"),
        Menu("Forum Post") / "forumpost" >> LocGroup("extra"),
        Menu("Dziennik") / "vregister" >> LocGroup("public"),
        Menu("Edytor postów") / "editpost" >> LocGroup("extra"),
        Menu("Editor stron") / "editpage" >> LocGroup("extra"),
        Menu("Stałe strony") / "admin" / "pages" >> LocGroup("admin") >> isAdmin,
        Menu("Działy forum") / "admin" / "forum" >> LocGroup("admin") >> isAdmin,
        Menu("Zmiana hasła") / "admin" / "password" >> LocGroup("admin") >> isAdmin,
        Menu("Sekretariat") / "admin" / "secretariat" >> LocGroup("admin") >> isAdmin,
        Menu("Maile kontaktowe") / "admin" / "contactmail" >> LocGroup("admin") >> isAdmin,
        Menu("Indeksowanie Picasa") / "admin" / "picasaindex" >> LocGroup("admin") >> isAdmin,
        Menu("Img") / "imgstorage" >> LocGroup("extra"),
        Menu("File") / "filestorage" >> LocGroup("extra"),
        Menu("Nauczyciele") / "secretariat" / "index" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Klasy") / "secretariat" / "classes" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Wychowawcy") / "secretariat" / "bringup" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Uczniowie") / "secretariat" / "pupils" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Przydział klas") / "secretariat" / "pupiltoclass" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Przedmioty") / "secretariat" / "subjects" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Test") / "test" >> LocGroup("extra")) :::
        // Menu entries for the User management stuff
        User.sitemap: _*)

    LiftRules.setSiteMapFunc(sitemap)

    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    LiftRules.passNotFoundToChain = true

    LiftRules.liftRequest.append {
      case Req("extra" :: _, _, _) => false
    }

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}