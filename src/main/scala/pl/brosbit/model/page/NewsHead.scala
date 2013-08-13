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


object NewsHead extends MongoDocumentMeta[NewsHead] {
  override def collectionName = "newshead"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new NewsHead(ObjectId.get, "", "", 0L, Nil, "", "", new ObjectId("000000000000000000000000"))
}

case class NewsHead(var _id: ObjectId, var title:String,
					 var authorName:String, var authorId:Long, var tags:List[String],
					 var thumbnailLink:String, var introduction:String,
					 var content:ObjectId )
					extends MongoDocument[NewsHead] {
  def meta = NewsHead
}