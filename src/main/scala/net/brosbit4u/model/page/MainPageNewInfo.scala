/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Slide(id:Int, src:String, link:String, description:String)
case class NewInForum(link:String, title:String, date:String)
case class NewInNews(link:String, title:String)
case class LinkItem(link:String, description:String)
case class LinkGroup(department:String, var links:List[LinkItem])
case class Anounce(id:Int, title:String, content:String)

//authors - last is older
object MainPageNewInfo extends MongoDocumentMeta[MainPageNewInfo] {
  override def collectionName = "maininfo"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new MainPageNewInfo(ObjectId.get, Nil, Nil, Nil, Nil,Nil)
}

case class MainPageNewInfo(var _id: ObjectId,  var slides:List[Slide], 
							var forum:List[NewInForum], var news: List[NewInNews], 
							var links: List[LinkGroup], var anounces: List[Anounce])	 
							extends MongoDocument[MainPageNewInfo] {
  def meta = MainPageNewInfo
}

