/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object ArticleContent extends MongoDocumentMeta[ArticleContent] {
  override def collectionName = "articlecontent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ArticleContent(ObjectId.get, "")
}

case class ArticleContent(var _id: ObjectId, var content:String) 
							extends MongoDocument[ArticleContent] {
  def meta = ArticleContent
}