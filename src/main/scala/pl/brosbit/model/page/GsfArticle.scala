/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object GsfArticle extends MongoDocumentMeta[GsfArticle] {
  override def collectionName = "gsfarticle"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = GsfArticle(ObjectId.get, "Brak artykułów", 0, "")
}

case class GsfArticle(var _id: ObjectId, var title:String, var order:Int,
					    var content:String ) extends MongoDocument[GsfArticle] {
  def meta = GsfArticle
}

object OrderPage extends Ordering[GsfArticle] {
  def compare(a:GsfArticle, b:GsfArticle) = a.order compare b.order
}