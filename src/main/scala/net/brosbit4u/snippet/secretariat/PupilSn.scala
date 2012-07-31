/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u {
  package snippet {

    import java.util.{ Date, GregorianCalendar, TimeZone }
    import scala.xml.{ NodeSeq, Text, XML }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._
    import _root_.net.brosbit4u.model._
    import _root_.net.brosbit4u.lib.Formater
    import  _root_.net.liftweb.http.js.JsCmds._
    import  _root_.net.liftweb.http.js.JsCmd
    import  _root_.net.liftweb.http.js.JE._

    class PupilSn {

      def pupilList() = {

        val pupils = User.findAll(By(User.role, "u"))
        "tr" #> pupils.map(pupil => {
          "tr [class]" #> { if (pupil.scratched.is)  "scratched" else ""} &
            ".id" #> <td>{ pupil.id.is.toString }</td> &
            ".firstname" #> <td>{ pupil.firstName.is }</td> &
            ".lastname" #> <td>{ pupil.lastName.is }</td> &
            ".birthdate" #> <td>{ Formater.formatDate(pupil.birthDate.is) }</td> &
            ".classInfo" #> <td>{pupil.classInfo.is}</td> &
            ".pesel" #> <td>{ pupil.pesel.is }</td>      
        })
      }
    
   def editAjax() = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var birthDate = ""
    var pesel = ""
    var classId = ""
    var errorInfo = ""
      
    def save() =  {
      val pupil = User.find(id).openOr(User.create) 
      ClassModel.find(classId) match {
        case Full(classModel) => {
          pupil.classInfo(classModel.classString()).
          	classId(classModel.id)
        }
        case _ =>
      }
      pupil.birthDate(Formater.fromStringToDate(birthDate)).firstName(firstName).
        	lastName(lastName).pesel(pesel).scratched(false).role("u").
        	password(Helpers.randomString(10)).save   
        id = pupil.id.toString
        JsFunc("$dTable.insertRow", id).cmd
    }
    
    def delete() = {
      User.find(id) match {
        case Full(user) => {
          user.scratched(true).email("").save
          JsFunc("$dTable.deleteRow", id).cmd
        }
        case _ => Alert("Nie ma takiego ucznia")
      }
    }
    val classList = ClassModel.findAll.map(classModel => (classModel.id.toString,classModel.classString()))

    val form = "#id" #> SHtml.text(id, id = _, "readonly"-> "readonly") &
       "#lastname" #> SHtml.text(lastName, lastName = _) &
        "#firstname" #> SHtml.text(firstName, firstName = _) &
       "#birthdate" #>   SHtml.text(birthDate, birthDate = _) &
       "#pesel" #> SHtml.text(pesel, pesel = _) &
       "#classInfo" #> SHtml.select(classList, Full(""), classId = _ ) &
       "#addInfo *" #> errorInfo &
       "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type"->"image", 
           "onclick" -> "return confirm('Na pewno usunąć klasę?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image",
          "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

      "form" #> (in => form(in))
   }

    }

  }
} // koniec packages

