/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u {
  package snippet {

    import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
    import _root_.scala.xml.{ NodeSeq, Text, XML }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._
    import net.brosbit4u.model.{ User, SubjectName, SubjectChangeList }
     import  _root_.net.liftweb.http.js.JsCmds._
  import  _root_.net.liftweb.http.js.JsCmd
  import  _root_.net.liftweb.http.js.JE._

    class SubjectSn {

      def subjectList() = {
        //var out:NodeSeq = NodeSeq.Empty
        val subjects: List[SubjectName] = SubjectName.findAll(OrderBy(SubjectName.nr, Ascending))
        "tr" #> subjects.map(subject => {
          "tr [class]" #> { if (subject.scratched.is)  "scratched" else "" } &
            ".id" #> <td>{ subject.id.is.toString }</td> &
            ".longname" #> <td>{ subject.name.is }</td> &
            ".shortname" #> <td>{ subject.short.is }</td> &
            ".ordernumber" #> <td>{ subject.nr.is.toString }</td>
        })
      }
  

      def editAjax() = {
    var id = ""
    var longName = ""
    var shortName = ""
    var ordernumber = ""
    var errorInfo = ""
      
    def save() =  {
      val subject = SubjectName.find(id).openOr(SubjectName.create) 
      val number = tryo(ordernumber.toInt).openOr(36)
     subject.name(longName.trim).short(shortName.trim).nr(number).scratched(false).save
     id = subject.id.toString
        JsFunc("$dTable.insertRow", id).cmd
    }
    
    def delete() = {
      SubjectName.find(id) match {
        case Full(subject) => {
          subject.scratched(true).save
          JsFunc("$dTable.deleteRow", id).cmd
        }
        case _ => Alert("Nie ma takiego przedmiotu")
      }
    }
    val numbers= (1 to 32).toList.map(i => (i.toString, i.toString))

    val form = "#id" #> SHtml.text(id, id = _, "readonly"-> "readonly") &
       "#longname" #> SHtml.text(longName, longName = _) &
        "#shortname" #> SHtml.text(shortName, shortName = _) &
       "#ordernumber" #> SHtml.select(numbers, Full("32"), ordernumber = _ ) &
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
