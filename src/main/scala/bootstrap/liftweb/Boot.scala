/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{ DB, By, ConnectionManager, ConnectionIdentifier, Schemifier, DefaultConnectionIdentifier }
import java.sql.{ Connection, DriverManager }
import _root_.net.brosbit4u.model._
import _root_.net.brosbit4u.api._
import javax.mail._
import javax.mail.internet._
import _root_.net.liftweb.mongodb._

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

    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("127.0.0.1", 27017), "vregister"))

    // where to search snippet
    LiftRules.addToPackages("net.brosbit4u")
    LiftRules.addToPackages("net.brosbit4u.snippet.page")

    Schemifier.schemify(true, Schemifier.infoF _, User, 
      ClassModel, MarkMap, SubjectName)
      
     LiftRules.statelessDispatchTable.append({
      case Req("img" :: id :: Nil, _, GetRequest) => () => ImageLoader.image(id)
      case Req("file" :: id :: Nil, _, GetRequest) => () => FileLoader.file(id)
    })
      
      def configMailer(host: String, user: String, password: String) {
      // Enable TLS support
      System.setProperty("mail.smtp.starttls.enable", "true");
      // Set the host name
      System.setProperty("mail.smtp.host", host) // Enable authentication
      System.setProperty("mail.smtp.auth", "true") // Provide a means for authentication. Pass it a Can, which can either be Full or Empty
      Mailer.authenticator = Full(new Authenticator {
        override def getPasswordAuthentication = new PasswordAuthentication(user, password)
      })
    }


    if (DB.runQuery("select * from users where lastname = 'Administrator'")._2.isEmpty) {
      val u = User.create
      u.lastName("Administrator").role("a").password("123qwerty").email("mail@mail.org").validated(true).save
    }

    val loggedIn = If(() => User.loggedIn_? && User.currentUser.open_!.validated.is,
      () => RedirectResponse("/user_mgt/login"))
      
    val isAdmin = If(() => User.loggedIn_? && (User.currentUser.open_!.role.is == "a"),
      () => RedirectResponse("/user_mgt/login"))
      
    val isSecretariat = If(() => {
      User.currentUser match {
        case Full(user) => {
          val role = user.role.is
          role  == "a" || role == "s" 
        }
        case _ => false
      }
        } , () => RedirectResponse("/user_mgt/login"))
      
    val isTeacher = If(() => {
      User.currentUser match {
        case Full(user) => {
          val role = user.role.is
         role == "n" || role  == "a" || role == "s" || role == "d"
        }
        case _ => false
        }
      } , () => RedirectResponse("/user_mgt/login"))

    // Build SiteMap::
    def sitemap() = SiteMap(
      List(
        Menu("Strona główna") / "index" >> LocGroup("public"), // Simple menu form
        Menu("Artykuły") / "pages" >> LocGroup("public"),
        Menu("Galeria") / "gallery" / ** >> LocGroup("public"),
        Menu("Kontakt") / "contact" >> LocGroup("public"),
        Menu("Forum") / "forum" >> LocGroup("public"),
        Menu("Edycja wątku") / "editthread" / ** >> LocGroup("extra") >> loggedIn,
        Menu("Forum Post") / "forumpost" / ** >> LocGroup("extra"),
        Menu("Dziennik") / "vregister" >> LocGroup("public"),
        Menu("Edytor artykułów") / "editpage" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Maile kontaktowe") / "admin" / "index" >> LocGroup("admin") >> isAdmin,
        Menu("Stałe strony") / "admin" / "pages" >> LocGroup("admin") >> isAdmin,
        Menu("Ogłoszenia") / "admin" / "anounces" >> LocGroup("admin") >> isAdmin,
        Menu("Działy forum") / "admin" / "forum" >> LocGroup("admin") >> isAdmin,
        Menu("Linki") / "admin" / "links" >> LocGroup("admin") >> isAdmin,
        Menu("Slajdy") / "admin" / "slides" >> LocGroup("admin") >> isAdmin,
        Menu("Zmiana hasła") / "admin" / "password" >> LocGroup("admin") >> isAdmin,
        Menu("Sekretariat") / "admin" / "secretariat" >> LocGroup("admin") >> isAdmin,
        Menu("Indeksowanie Picasa") / "admin" / "picasaindex" >> LocGroup("admin") >> isAdmin,
        Menu("Img") / "imgstorage" >> LocGroup("extra") >> loggedIn,
        Menu("Thumb") / "thumbstorage" >> LocGroup("extra") >> loggedIn,
        Menu("File") / "filestorage" >> LocGroup("extra") >>loggedIn,
        Menu("Nauczyciele") / "secretariat" / "index" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Klasy") / "secretariat" / "classes" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Uczniowie") / "secretariat" / "pupils" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Przedmioty") / "secretariat" / "subjects" >> LocGroup("secretariat") >> isSecretariat,
        //Menu("Dzwonki") / "secretariat" / "bells" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Wybór dziennika") / "teacher" / "index" / ** >> LocGroup("teacher") >> isTeacher,
        Menu("Dane ucznia") / "teacher" / "pupil_data" / **  >> LocGroup("teacher") >> isTeacher,
        Menu("Rodzice ucznia") / "teacher" / "parent_data" / ** >> LocGroup("teacher") >> isTeacher,
        Menu("Tematy") / "teacher" / "themes"/ ** >> LocGroup("teacher") >> isTeacher,
        Menu("Obecności") / "teacher" / "absents"/ ** >> LocGroup("teacher") >> isTeacher,
        Menu("Oceny") / "teacher" / "marks"/ ** >> LocGroup("teacher") >> isTeacher,
        Menu("Uwagi") / "teacher" / "opinions"/ ** >> LocGroup("teacher") >> isTeacher,
        Menu("Twój plan") / "teacher" / "teacher_plan" >> LocGroup("teacher") >> isTeacher,
        Menu("Plan klasy") / "teacher" / "class_plan" / ** >> LocGroup("teacher") >> isTeacher,
        Menu("Test") / "test" >> LocGroup("extra")) :::
        // Menu entries for the User management stuff
        User.sitemap: _*)

    LiftRules.setSiteMapFunc(sitemap)
    
    LiftRules.statelessRewrite.prepend(NamedPF("ClassRewrite") {
        case RewriteRequest(
            ParsePath("gallery" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "gallery" ::  Nil, Map("id" -> id)  )	
         case RewriteRequest(
            ParsePath("forumpost" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "forumpost" ::  Nil, Map("id" -> id)  )	
        case RewriteRequest(
            ParsePath("forum" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "forum" ::  Nil, Map("id" -> id)  )	
        case RewriteRequest(
            ParsePath("editthread" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "editthread" ::  Nil, Map("id" -> id)  )
        case RewriteRequest(
            ParsePath("editpage" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "editpage" ::  Nil, Map("id" -> id)  )
		case RewriteRequest(
            ParsePath("teacher" :: "index" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "index" :: Nil, Map("class" -> classSchool)  )			
        case RewriteRequest(
            ParsePath("teacher" :: "pupil_data" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "pupil_data" :: Nil, Map("class" -> classSchool)  )
         case RewriteRequest(
            ParsePath("teacher" :: "parent_data" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "parent_data" :: Nil, Map("class" -> classSchool)  )
         case RewriteRequest(
            ParsePath("teacher" :: "themes" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "themes" :: Nil, Map("class" -> classSchool)  )
          case RewriteRequest(
            ParsePath("teacher" :: "absents" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "absents" :: Nil, Map("class" -> classSchool)  )
          case RewriteRequest(
            ParsePath("teacher" :: "marks" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "marks" :: Nil, Map("class" -> classSchool)  )
          case RewriteRequest(
            ParsePath("teacher" :: "opinions" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "opinions" :: Nil, Map("class" -> classSchool)  )
         case RewriteRequest(
            ParsePath("teacher" :: "class_plan" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "class_plan" :: Nil, Map("class" -> classSchool)  )
      })

    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    LiftRules.passNotFoundToChain = true
    
    configMailer("smtp.gmail.com", "20logdansk@gmail.com","noting")

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