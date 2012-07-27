/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import java.util.Date
import scala.xml.{ NodeSeq, Text, XML, Unparsed }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import _root_.net.liftweb.mapper.{ Ascending, OrderBy, By }
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import Helpers._
import org.bson.types.ObjectId

class AdminPagesSn {

  object notice extends RequestVar[String]("")
  ///!!!!!!dodać wszędzie sprawdzanie czy to admin

  //zrobić walidację maila

  def addDepartment() = {
    //      if (!isAdmin_?) return <h1>Nie masz uprawnień do edycji!</h1>
    var id = ""
    var name = ""

    def addDepartment() {
       PageDepartment.find(id.trim) match {
        case Some(pageDepartment) => {
          pageDepartment.name = name
          pageDepartment.save
        }
        case _ => {
          val pageDepartment = PageDepartment.create
          pageDepartment.name = name
          pageDepartment.save
        }
      }
       S.redirectTo("/admin/pages")
    }
    
    
    def delDepartment(): Unit = {
        PageDepartment.find(id.trim) match {
          case Some(pageDepartment) => pageDepartment.delete
          case _ => 
        }
        S.redirectTo("/admin/pages")
    }

    "#id" #> SHtml.text(id, x => id = x, "style" -> "display:none;", "id" -> "id") &
      "#name" #> SHtml.text(name, x => name = x.trim, "maxlength" -> "30", "id" -> "name") &
      "#save" #> SHtml.submit("Zapisz!", addDepartment, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", delDepartment, 
          "onclick" -> "return confirm('Na pewno chcesz usunąć dział i wszystkie strony działu?');")
  }

  def departments(n: NodeSeq): NodeSeq = {
    val departments = PageDepartment.findAll
    var node: NodeSeq = <tbody>{
      for (department <- departments) yield {
        <tr onclick={ "setData(this)" } id={department._id.toString()} >
          <td>{department.name}</td>
        </tr>
      }
    }</tbody>
    node
  }

  ////////////////////////////////////////////////////////////////////
  /** dodawanie działu do forum*/
  def addForumDepartment() = {
    var id = ""
    var name = ""

    def save() = {
      val forumDepartment = ForumDepartment.find(id).getOrElse(ForumDepartment.create)
      forumDepartment.name = name
      forumDepartment.save
    }

    def delete(): Unit = {
        if (id.length > 20) {
          ForumDepartment.find(id) match {
            case Some(forumDepartment) => forumDepartment.delete
            case _ => 
          }
        }
      } 

    "#id" #> SHtml.text(id, x => id = x, "style" -> "display:none;", "id" -> "id") &
      "#name" #> SHtml.text(name, x => name = x.trim, "size" -> "40", "maxlength" -> "40", "id" -> "name") &
      "#save" #> SHtml.submit("Zapisz!", save, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", delete, 
          "onclick" -> "return confirm('Na pewno chcesz usunąć dział i wszystkie wątki w dziale?');")

  }

  def forumDepartments() = {
    "tr" #> ForumDepartment.findAll.map(forumDepartment => {
      <tr onclick={"setData(this)"} id={forumDepartment._id.toString} >
      <td>{forumDepartment.name}</td>
      </tr>
    })
  }

  //////////////////////////////////////////////////////////////////////
  //dodawanie adresów kontakowych mail do wysyłania maili ze strony
  def addContactMail() = {
    var id = ""
    var email = ""
    var description = ""
      
    def saveMail() {
         ContactMail.find(id) match {
        	case Some(contactMail) => {
        	    contactMail.description = description
        	    contactMail.mailAddress = email
        	    contactMail.save
        	}
        	case _ => {
        	   val contactMail = ContactMail.create
        	   contactMail.description = description
              contactMail.mailAddress = email
              contactMail.save
        	}
      }
    }

    def deleteMail() {
        ContactMail.find(id) match {
        	case Some(contactMail) => contactMail.delete
        	case _ =>
      }
    }

    "#id" #> SHtml.text(id, x => id = x, "size" -> "12", "style" -> "display:none;", "id" -> "id") &
      "#descript" #> SHtml.text(description, x => description = x.trim, "size" -> "40", "maxlength" -> "40", "id" -> "descript") &
      "#mail" #> SHtml.text(email, x => email = x.trim, "size" -> "40", "maxlength" -> "40", "id" -> "mail") &
      "#save" #> SHtml.submit("Zapisz!", saveMail, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", deleteMail, "onclick" -> "return confirm('Na pewno chcesz usunąć email?');") &
      "#notice" #> Text(notice)

  }

  //wyświetlanie listy adresów kontaktowych szkoły
  def contactMails() = {
    val contactMails = ContactMail.findAll
    "tr" #>  contactMails.map(contactMail => {
      <tr onclick="setData(this)" id={contactMail._id.toString} >
      <td>{contactMail.mailAddress}</td><td>{contactMail.description}</td></tr>
    })
  }

  //zmiana hasła admina
  def changePassword() = {
    var password1 = ""
    var password2 = ""
    var notice = ""
    def changePass() {
      if (password1 == "" || password1 != password2) return
      if (isAdmin_?) { if (User.currentUser.get.password(password1).save) notice = "Hasło zapisano" else notice = "Nieudany zapis hasła" }
    }

    "#password1" #> SHtml.text(password1, x => password1 = x.trim, "type" -> "password", "id" -> "pass1") &
      "#password2" #> SHtml.text(password2, x => password2 = x.trim, "type" -> "password", "id" -> "pass2") &
      "#submit" #> SHtml.submit("Zmień!", changePass, "onclick" -> "return checkPass();") &
      "#notice" #> Text(notice)
  }

  ///dodawanie linków
  def addLinks() = {
    var xmlDataStr = ""
    def add() {
      val mainPageNewInfo = getMainPageNewInfo()
      var allLinks:List[LinkGroup] = Nil
      var xmlData = XML.loadString(xmlDataStr)
      (xmlData \ "links").foreach(links => {
        val nameDep = (links \ "@name").toString
        val linkGroup = LinkGroup(nameDep,Nil)
        (links \ "link").foreach(link => {
          val title = (link \ "@title").toString
          val url = (link \ "@src").toString
          linkGroup.links = LinkItem(url, title) :: linkGroup.links 
        })
        allLinks = linkGroup :: allLinks
      })
      mainPageNewInfo.links = allLinks
      mainPageNewInfo.save
      S.redirectTo("/admin/links")
    }
    "#xmlData" #> SHtml.text(xmlDataStr, xmlDataStr = _, "style" -> "display:none;") &
      "#submit" #> SHtml.submit("ZAPISZ", add, "onclick" -> "return saveAll()")
  }
  // wyślietlanie konfiguracji linków
  def showLinks() = {
    val mainPageNewInfo = getMainPageNewInfo()
        ".divDepartment" #> mainPageNewInfo.links.map(linkGroup => {
      ".nameDep" #> <input value={linkGroup.department} type="text"/> &
      ".linkTr" #> linkGroup.links.map(link => {
        	".nameLink" #> <td>{ link.description }</td> &
            ".urlLink" #> <td>{ link.link}</td>
        	})
        })
    }
   

  def isAdmin_?(): Boolean = {
    User.currentUser match {
      case Full(user) => if (user.role.is == "a") true else false
      case _ => false
    }
  }

  def editSecretariat() = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""
    def save() {
      val user = User.find(id).openOr(User.create)
      if (id != "0" && id != "") {
        val userChangeList = UserChangeList.create
        userChangeList.firstName(user.firstName.is).lastName(user.lastName.is).email(user.email.is)
          .user(user).date(new Date()).save
      }
      user.firstName(firstName).lastName(lastName).email(email)
      				.role("s").save
    }
    def delete() {
      User.find(id) match {
        case Full(user) => user.validated(false).save
        case _ =>
      }
    }
    "#ID" #> SHtml.text(id, id = _, "readonly" -> "readonly", "id" -> "ID") &
    "#firstName" #> SHtml.text(firstName, firstName = _, "id" -> "firstName", "maxlength" -> "30") &
    "#lastName" #> SHtml.text(lastName, lastName = _, "id" -> "lastName", "maxlength" -> "40") &
    "#email" #> SHtml.text(email, email = _, "id" -> "email", "maxlength" -> "48") &
    "#submit" #> SHtml.submit("Dodaj", save, "onclick" -> "return validate()") &
    "#delete" #> SHtml.submit("Usuń", delete, "onclick" -> "return validID()")
  }

  def showSecretariat() = {
    val secretariatUsers = User.findAll(By(User.role, "s"))
    "tr" #> secretariatUsers.map(user => {
      <tr class={ if (user.validated.is) "normal" else "scratched" } onclick="edit(this)" title={
        UserChangeList.findAll(By(UserChangeList.user, user), OrderBy(UserChangeList.date, Ascending)).map(changeList => {
          changeList.date.toString + " " + changeList.lastName.is + " " + changeList.firstName.is + " " +
            changeList.email.is + " " + changeList.passStr
        }).mkString("<br />")
      }>
        <td>{ user.id.is.toString }</td>
        <td>{ user.lastName.is }</td>
        <td>{ user.firstName.is }</td>
        <td>{ user.email.is }</td>
      </tr>
    })
  }

  def addAnounce() = {
      var id = ""
	  var title = ""
	  var content = ""
	    
	  def save(){
        val idInt =  if(id == "") 0 else id.trim.toInt
        val mainPageNewInfo = getMainPageNewInfo()
	    var anounces = mainPageNewInfo.anounces
	    if(idInt <= 0) {
	      val idList = anounces.map(anounce => anounce.id)
	      val newId = if(idList.isEmpty) 1 else idList.max + 1
	      anounces = Anounce(newId, title.trim, content.trim)::anounces 
	    }
	    else {
	      val anounce = Anounce(idInt, title.trim, content.trim)
	      anounces = anounces.filter(_.id < idInt):::(anounce::anounces.filter(_.id > idInt))
	    }
	    
	    mainPageNewInfo.anounces = anounces
	    mainPageNewInfo.save
	  }
      
	  def delete(){
	    val idInt =  if(id == "") 0 else id.trim.toInt
	    if (idInt > 0){
          val mainPageNewInfo = getMainPageNewInfo()
          mainPageNewInfo.anounces match {
            case Nil =>
            case anounces => {
              mainPageNewInfo.anounces = anounces.filter(_.id < idInt) ::: anounces.filter(_.id > idInt)
              mainPageNewInfo.save
            }
          }
	    }
	  }
	  
	  "#id" #> SHtml.text(id, id = _, "id"->"id","style"->"display:none;") &
	  "#title" #> SHtml.text(title, title = _, "id"->"title","maxlength"-> "40") &
	  "#content" #> SHtml.textarea(content, content = _, "id"->"content","rows"->"10") & 
	  "#submit" #> SHtml.submit("Zapisz" ,save, "onclick"->"return validate();") &
	  "#delete" #> SHtml.submit("Usuń", delete, "onclick"->"return areYouSure();")
  }
  
  def anounceList() = {
    val mainPageNewInfo = getMainPageNewInfo()
    val anounces =  mainPageNewInfo.anounces
    "tr" #> anounces.map(anounce => {
    		<tr id={anounce.id.toString} onclick="setData(this);" ><td>{anounce.title}</td><td>{Unparsed(anounce.content)}</td></tr>
    	})
  }

  
  def addSlide() = {
    var id = ""
    var descript = ""
    var src = ""
    var link = ""
      
    def save(){
      val idInt =  if(id == "") 0 else try {id.trim.toInt} finally 0
      val mainPageNewInfo = getMainPageNewInfo()
      var slides = mainPageNewInfo.slides
      if(idInt <= 0) {
	      val idList = slides.map(slide => slide.id)
	      val newId = if(idList.isEmpty) 1 else idList.max + 1
	      slides = Slide(newId, src.trim, link.trim, descript.trim)::slides 
	    }
	    else {
	      val slide = Slide(idInt, src.trim, link.trim, descript.trim)
	      slides = slides.filter(_.id < idInt):::(slide::slides.filter(_.id > idInt))
	    }
	    
	    mainPageNewInfo.slides = slides
	    mainPageNewInfo.save
      
    }
    def delete() {
      val idInt =  if(id == "") 0 else id.trim.toInt
      
      if(idInt > 0){
    	val mainPageNewInfo = getMainPageNewInfo()
        mainPageNewInfo.slides match {
            case Nil =>
            case slides => {
              mainPageNewInfo.slides = slides.filter(_.id < idInt) ::: slides.filter(_.id > idInt)
              mainPageNewInfo.save
            }
          }
      }
    }
    
    "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    "#source" #> SHtml.text(src, src = _) &
    "#link" #> SHtml.text(link, link = _) &
    "#description" #> SHtml.text(descript, descript = _) &
    "#save" #> SHtml.submit("Zapisz!", save) &
    "#delete" #> SHtml.submit("Usuń!", delete) 
  }
  
  def slideList() = {
    val mainPageNewInfo = getMainPageNewInfo()
    val slides = mainPageNewInfo.slides
    "tr" #> slides.map(slide => {
    		<tr id={slide.id.toString} onclick="setData(this);" >
    		<td><img src={slide.src.toString} style="width:40%;height:40%;" /></td>
    		<td>{slide.link}</td><td>{slide.description}</td></tr>
    	})
  }
  
  private def getMainPageNewInfo() =  {
     val mainPageNewInfoList = MainPageNewInfo.findAll
     val mainPageNewInfo = if(mainPageNewInfoList.isEmpty) MainPageNewInfo.create 
        					  else mainPageNewInfoList.head
      mainPageNewInfo
  }

}

