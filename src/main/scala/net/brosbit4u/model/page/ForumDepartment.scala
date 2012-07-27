/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object ForumDepartment extends MongoDocumentMeta[ForumDepartment] {
  override def collectionName = "forumdepartment"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ForumDepartment(ObjectId.get, "")
}

case class ForumDepartment(var _id: ObjectId, var name:String) 
							extends MongoDocument[ForumDepartment] {
  def meta = ForumDepartment
}
