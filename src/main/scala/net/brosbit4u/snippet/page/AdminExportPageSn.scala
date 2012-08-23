/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.snippet.page

import java.util.Date
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.JsonAST._

class AdminExportPageSn {
 
  var what = ""
    
	def export = {
	   
       var department = ""
       var title = ""
	  
       def save(){
	    val user = User.currentUser.get
	    val pageHead = PageHead.create
	    val pageContent = ArticleContent.create
	    pageContent.content = createPageContentSwitcher
	    pageContent.save
	    pageHead.title = title.trim
	    pageHead.authorName = user.getFullName
	    pageHead.authorId = user.id.is
	    pageHead.department = department
	    pageHead.content = pageContent._id
	    pageHead.save
	    S.redirectTo("/pages?w=p&id=" + pageHead._id)
	  }   
         
	  val departments = PageDepartment.findAll.map(department => (department.name, department.name))
	  val actions = List(("p","Plan lekcji"),("e","Zajęcia pozalekcyjne"))
	  "#what" #> SHtml.select(actions, Full(actions.head._1) , what = _) &
	  "#department" #> SHtml.select(departments, Full(""), department = _ ) &
	  "#title" #> SHtml.text(title, title = _) &
	  "#save" #> SHtml.submit("Utwórz", save)
	}
	
	
	
	private def createPageContentSwitcher = {
	  what match {
	    case "p" => createPageContentForPlan
	    case "e" => createPageContentForExtraLessons
	    case _ => "BŁĄD!!!"
	  }
	}
	
	
	private def createPageContentForPlan = {
	  
	  val row = """<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>"""
	  val plans = PlansOfClass.findAll(JObject(Nil),("classId"->1))
	  val bells = BellsData.getOrCreate
	  plans.map(plan => {
	    """<div id="%s" class="lessonPlan"><h2>Klasa %s</h2>""".format(plan.classStr, plan.classStr) +
	    """<table><thead><tr><th>Numer</th><th>Godziny</th><th>Poniedziałek</th><th>Wtorek</th>
			  <th>Środa</th><th>Czwartek</th><th>Piątek</th></tr></thead><tbody>""" + 
	    (0 to 10).toList.map(i =>{
	      row.format(i, bells.getLessonTime(i),
	    		  (plan.day1(i).subject + " " + plan.day1(i).info + " " + plan.day1(i).room),
	    		  (plan.day2(i).subject + " " + plan.day2(i).info + " " + plan.day2(i).room),
	    		  (plan.day3(i).subject + " " + plan.day3(i).info + " " + plan.day3(i).room),
	    		  (plan.day4(i).subject + " " + plan.day4(i).info + " " + plan.day4(i).room),
	    		  (plan.day5(i).subject + " " + plan.day5(i).info + " " + plan.day5(i).room))
	    }).mkString +
	     """</tbody></table></div><br/><br/><br/><br/>"""
	  }).mkString
	    
	}
	
	
	
	private def createPageContentForExtraLessons = {
	  
	  val row = """<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>"""
	  val extraLessons = ExtraLessons.findAll
	  var i = 0;
	  
	    """<table><thead><tr><th>Lp.</th><th>Temat</th><th>Prowadzący</th><th>Opis</th>
			  <th>Kiedy</th></tr></thead><tbody>""" + 
	      extraLessons.map(extraLesson => {
	        i += 1
	      row.format(i, extraLesson.title, extraLesson.teacherName, extraLesson.description, extraLesson.when)
	    }).mkString +
	     """</tbody></table></div><br/>"""
	    
	}
	
	
	
}