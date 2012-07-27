/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Comment(id:Int, authorName:String, authorId:Long, date:String, content:String)

object ForumThreadContent extends MongoDocumentMeta[ForumThreadContent] {
  override def collectionName = "forumthreadcontent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ForumThreadContent(ObjectId.get, "", Nil)
}

case class ForumThreadContent(var _id: ObjectId,
					 		var content:String, var comments:List[Comment]) 
							extends MongoDocument[ForumThreadContent] {
  def getLastInfo() = {
     if(comments.length > 0) {
       val lastComment = comments.last
       "<span class='fullname'>%s<span><br/><span class='date'>%s<span>".format(lastComment.authorName,lastComment.date)
       }
     else ""
  }
  def meta = ForumThreadContent
}


