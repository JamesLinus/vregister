/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u.snippet.page

import _root_.net.brosbit4u.model.page._
import _root_.net.brosbit4u.model._

trait UpdateMainPageInfo {
	def updateForumInfo(date:String, title:String, id:String){
	  val mainPageNewInfo = MainPageNewInfo.findAll match {
	    case Nil => MainPageNewInfo.create
	    case list => list.head
	  }
	  val link = "/forumpost/" + id
	 mainPageNewInfo.forum = NewInForum(link, title, date)::mainPageNewInfo.forum.filter(forum => forum.link != link )
	 mainPageNewInfo.save
	}
}