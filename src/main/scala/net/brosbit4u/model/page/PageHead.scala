/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object PageHead extends MongoDocumentMeta[PageHead] {
  override def collectionName = "pagehead"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = PageHead(ObjectId.get, "", "", "", 0L, new ObjectId("000000000000000000000000"))
}

case class PageHead(var _id: ObjectId, var title:String,
					 var department:String, var authorName:String,  
					 var authorId:Long, var content:ObjectId )
					extends MongoDocument[PageHead] {
  def meta = PageHead
}