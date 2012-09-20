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
import _root_.net.brosbit4u.lib.MailConfig
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
import _root_.net.liftweb.json.JsonDSL._

class AdminPagesSn {

  object notice extends RequestVar[String]("")

  def addDepartment() = {
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
        <tr ondblclick={ "setData(this)" } id={department._id.toString()} >
          <td>{department.name}</td>
        </tr>
      }
    }</tbody>
    node
  }

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
      <tr ondblclick={"setData(this)"} id={forumDepartment._id.toString} >
      <td>{forumDepartment.name}</td>
      </tr>
    })
  }


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
      "#descript" #> SHtml.text(description, x => description = x.trim, "maxlength" -> "40", "id" -> "descript") &
      "#mail" #> SHtml.text(email, x => email = x.trim, "maxlength" -> "60", "id" -> "mail") &
      "#save" #> SHtml.submit("Zapisz!", saveMail, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", deleteMail, "onclick" -> "return confirm('Na pewno chcesz usunąć email?');") &
      "#notice" #> Text(notice)

  }

  /** school contacts mail */
  def contactMails() = {
    val contactMails = ContactMail.findAll
    "tr" #>  contactMails.map(contactMail => {
      <tr ondblclick="setData(this)" id={contactMail._id.toString} >
      <td>{contactMail.mailAddress}</td><td>{contactMail.description}</td></tr>
    })
  }

  
  def adminsList() = {
    "tbody" #> User.findAll(By(User.role,"a")).map(admin => {
        <tr ondblclick="insertFromTableToForm(this);">
    	<td>{admin.id.toString}</td><td>{admin.firstName}</td>
    	<td>{admin.lastName}</td><td>{admin.email}</td>
    	</tr> 
    })
  }

  def editAdmin() = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""
    var password1 = ""
    var password2 = ""
    var notice = ""
    def save() {
      val user = User.find(id).openOr(User.create)
      user.firstName(firstName).lastName(lastName).email(email)
      if (password1 != "" && password1 == password2 ) user.password(password1)
      user.role("a").validated(true).save
    }
    def delete() {
      val numberAdmins = User.findAll(By(User.role,"a")).length
      if(numberAdmins > 1) {
        User.find(id) match {
          case Full(user) => user.delete_!
          case _ =>
        }
      }
      else {
        notice = "Nie można usunąć jedynego admistratora"
      }
    }
    "#id" #> SHtml.text(id, id = _) &
    "#firstname" #> SHtml.text(firstName, firstName = _) &
    "#lastname" #> SHtml.text(lastName, lastName = _) &
    "#email" #> SHtml.text(email, email = _) &
    "#password1" #> SHtml.text(password1, x => password1 = x.trim, "type" -> "password") &
      "#password2" #> SHtml.text(password2, x => password2 = x.trim, "type" -> "password") &
      "#save" #> SHtml.submit("Zmień!", save) &
      "#delete" #> SHtml.submit("Usuń!", delete) &
      "#notice" #> Text(notice)
  }

  def addLinks() = {
    var xmlDataStr = ""
    def add() {
      val mainPageLinks = getMainPageLinks
      var linkDepartments:List[LinkDepartment] = Nil
      var xmlData = XML.loadString(xmlDataStr)
      (xmlData \ "links").foreach(links => {
        val nameDep = (links \ "@name").toString
        var linksList:List[Link] = Nil 
        (links \ "link").foreach(link => {
          val title = (link \ "@title").toString
          val url = (link \ "@src").toString
          linksList = Link(url, title) :: linksList 
        })
        linkDepartments = LinkDepartment(nameDep, linksList) :: linkDepartments
      })
      mainPageLinks.links = linkDepartments
      mainPageLinks.save
      S.redirectTo("/admin/links")
    }
    "#xmlData" #> SHtml.text(xmlDataStr, xmlDataStr = _, "style" -> "display:none;") &
      "#submit" #> SHtml.submit("ZAPISZ", add, "onclick" -> "return saveAll()")
  }

  def showLinks() = {
    val mainPageLinks = getMainPageLinks
   
        ".divDepartment" #> mainPageLinks.links.map(linkGroup => {
      ".nameDep" #> <input value={linkGroup.name} type="text"/> &
      ".linkTr" #> linkGroup.links.map(link => {
        	".nameLink" #> <td>{ link.title}</td> &
            ".urlLink" #> <td>{ link.url}</td>
        	})
        })
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
      <tr class={ if (user.validated.is) "normal" else "scratched" } ondblclick="edit(this)" title={
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
	    val mainPageData = MainPageData.find(id) match {
          case Some(mainPageData) => mainPageData
          case _ => {
            val tmpMainPageData = MainPageData.create
            tmpMainPageData.key = Keys.anounce.toString
            tmpMainPageData
          }
        }
	    mainPageData.title = title.trim
	    mainPageData.content = content.trim
	    mainPageData.save
	  }
      
	  def delete(){
	    MainPageData.find(id) match {
	      case Some(mainPageData) => mainPageData.delete
	      case _ =>
	    }
	  }
	  
	  "#id" #> SHtml.text(id, id = _, "id"->"id","style"->"display:none;") &
	  "#title" #> SHtml.text(title, title = _, "id"->"title","maxlength"-> "40") &
	  "#content" #> SHtml.textarea(content, content = _, "id"->"content","rows"->"10") & 
	  "#submit" #> SHtml.submit("Zapisz" ,save, "onclick"->"return validate();") &
	  "#delete" #> SHtml.submit("Usuń", delete, "onclick"->"return areYouSure();")
  }
  
  def anounceList() = {
    val anounces = getMainPageDataWithAnounces
    "tr" #> anounces.map(anounce => {
    		<tr id={anounce._id.toString} ondblclick="setData(this);" ><td>{anounce.title}</td><td>{Unparsed(anounce.content)}</td></tr>
    	})
  }

  
  def addSlide() = {
    var id = ""
    var descript = ""
    var src = ""
    var link = ""
      
    def save(){
      val mainPageData = MainPageData.find(id) match {
        case Some(mainPageData) => mainPageData
        case _ => {
          val tmpMainPageData = MainPageData.create
          tmpMainPageData.key = Keys.slide.toString
          tmpMainPageData
        }
      }
      mainPageData.title = descript
      mainPageData.src = src
      mainPageData.link = link
      mainPageData.save
    }
    
    def delete() {
      MainPageData.find(id) match {
        case Some(mainPageData) => mainPageData.delete
        case _ =>
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
    val slides = getMainPageDataWithSlides
    "tr" #> slides.map(slide => {
    		<tr id={slide._id.toString} ondblclick="setData(this);" >
    		<td><img src={slide.src} style="width:300px;height:100px;" /></td>
    		<td>{slide.link}</td><td>{slide.title}</td></tr>
    	})
  }
  
  
  
  def addMailConfig() = {
    val mailConfig = new MailConfig
    var (host,user,password) = mailConfig.getConfig
    
    def save(){
      mailConfig.configureMailer(host, user, password)
    }
    
     "#host" #> SHtml.text(host, host = _) &
    "#user" #> SHtml.text(user, user = _) &
    "#password1" #> SHtml.password(password, password = _) &
    "#save" #> SHtml.submit("Zapisz!", save) 
  }
  
  private def getMainPageDataWithSlides = MainPageData.findAll(("key"->Keys.slide.toString))
  
  private def getMainPageDataWithAnounces = MainPageData.findAll(("key"->Keys.anounce.toString))
  
 private def getMainPageLinks = MainPageLinks.findAll match {
    case head::list => head
    case _ => MainPageLinks.create
  }

}

